# 구현 전략 01 — Product + Brand 조합 도메인 서비스

> **작성일:** 2026-05-23
> **목적:** 체크리스트 항목 "상품 상세 조회 시 Product + Brand 정보 조합은 **도메인 서비스**에서 처리했다"를 충족하기 위한 설계 격차 해소.
> **현재 docs 상태:** `03-class-diagram.md` §3은 `ProductFacade.getProduct()`(application layer)가 조합 책임을 가진다고 정의 — 체크리스트 요구와 불일치.
> **전략:** **바텀업** — VO/Model 부터 단단히 만들고 위 레이어를 쌓는다.

---

## 1. 문제 정의

| 구분 | 현재 설계 | 체크리스트 요구 |
|------|-----------|----------------|
| Product + Brand 조합 책임 | `ProductFacade` (application) | **도메인 서비스** |
| 도메인 서비스 정의 | "Repository 사용, 상태 없음" — 단일 애그리거트 CRUD 중심 (`ProductService`, `BrandService`) | 도메인 객체 간 **협력 중심** |
| 결과 DTO | `ProductInfo(brandId, brandName, ...)` 조립을 Facade가 수행 | 도메인 서비스가 두 애그리거트를 결합한 결과를 반환 |

`Brand`와 `Product`는 독립 애그리거트(03-class-diagram.md §3 분리 근거)인데, 상품 상세 조회는 두 애그리거트 정보를 함께 보여줘야 한다. **두 애그리거트 간 협력 = 도메인 서비스의 정의**다.

---

## 2. 도입할 도메인 서비스

### `ProductDetailService` (신규)

위치: `com.loopers.domain.product.ProductDetailService`

```
<<DomainService>>
class ProductDetailService {
    -productRepository: ProductRepository
    -brandRepository: BrandRepository

    +loadDetail(productId): ProductDetail
    // ProductModel + BrandModel을 함께 로딩해
    // 두 애그리거트를 결합한 ProductDetail(도메인 값)을 반환
}
```

**책임:**
- `Product` 조회 → 해당 `brandId`로 `Brand` 조회 → 두 애그리거트를 묶어 `ProductDetail` 반환
- 상태 없음(stateless), Repository 두 개에만 의존
- 트랜잭션 경계는 application(Facade)이 결정 — 도메인 서비스는 트랜잭션을 열지 않는다

### `ProductDetail` (신규 도메인 값)

위치: `com.loopers.domain.product.ProductDetail`

```
<<ValueObject>> (또는 도메인 리드모델)
class ProductDetail {
    -product: ProductModel
    -brand: BrandModel

    +product(): ProductModel
    +brand(): BrandModel
}
```

> `ProductInfo`(application DTO)는 그대로 유지. `ProductInfo.from(ProductDetail)`로 변환만 위임.

---

## 3. 레이어별 변경점

| 레이어 | 변경 |
|--------|------|
| `domain/product` | `ProductDetailService` 신설, `ProductDetail` 신설 |
| `domain/product` | `BrandRepository`는 이미 `domain/brand` 또는 `domain/product`에 있음 — 도메인 서비스가 동일 패키지에서 import. 패키지 경계는 `ArchitectureTest`가 허용 (도메인끼리 참조 가능) |
| `application/product` | `ProductFacade.getProduct(id)`가 `productDetailService.loadDetail(id)` 호출 후 `ProductInfo.from(detail)` 반환. 더 이상 두 Service를 조율하지 않음 |
| `interfaces/api/product` | 변경 없음 |

---

## 4. 구현 순서 (바텀업)

```
1. ProductDetail (도메인 값) — POJO + 단위 테스트
   ↓
2. ProductDetailService — 단위 테스트 (Fake Repository 2개로)
   ↓
3. ProductFacade.getProduct() 리팩터 — 통합 테스트로 회귀 확인
   ↓
4. 03-class-diagram.md / 00-domain-spec.md docs 갱신
```

---

## 5. 단위 테스트 포인트 (`ProductDetailServiceTest`)

| # | 시나리오 | 기대 |
|---|---------|------|
| 1 | 정상: ACTIVE Product + ACTIVE Brand | `ProductDetail` 반환, 두 모델 모두 포함 |
| 2 | Product 없음 | `ProductNotFoundException` |
| 3 | Brand가 DELETED 상태 | 비즈니스 정책 결정 — 본 문서 §6 참조 |
| 4 | Brand 없음 (데이터 불일치) | `BrandNotFoundException` — 내부 에러로 처리 |

테스트는 `FakeProductRepository`, `FakeBrandRepository`로 외부 의존성 분리. 상세 패턴은 [구현 전략 02](./02-unit-test-strategy.md) 참조.

---

## 6. 미결 사항 (공동 결정 필요)

- **DELETED Brand의 Product 상세 조회**: 현재 `00-domain-spec.md`는 Brand 삭제 시 Product도 cascade 소프트 삭제 — 조회 가능 여부 자체가 차단되어야 함. 단, 데이터 일시 불일치 구간은 어떻게 처리할지 결정 필요.
- **도메인 서비스 명명**: `ProductDetailService` vs `ProductCompositionService` — 팀 컨벤션 확인 필요.

---

## 7. 회수 시그널 (도메인 서비스 → Facade 복귀)

다음 중 하나라도 만족하면 도메인 서비스를 다시 Facade로 흡수 검토:

- `ProductDetail`이 다른 도메인 모듈(Order, Like)에서 한 번도 사용되지 않음 → 단일 사용처면 Facade 충분
- `ProductDetailService` 메서드가 1개로 유지되고 협력 로직이 단순 조회뿐이면 도메인 서비스의 부가가치가 낮음

현재는 체크리스트 충족이 명확한 가치이므로 도입.
