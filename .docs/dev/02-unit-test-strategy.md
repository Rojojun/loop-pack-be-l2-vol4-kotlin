# 구현 전략 02 — 단위 테스트 전략 (Like / Order / Fake·Stub)

> **작성일:** 2026-05-23
> **목적:** 체크리스트의 다음 항목을 코드로 충족하기 위한 단위 테스트 가이드.
> - 좋아요 등록/취소 흐름 단위 테스트
> - 정상 주문 / 예외 주문 흐름 단위 테스트
> - 외부 의존성 분리 + Fake/Stub 기반 단위 테스트
> **전략:** **바텀업** — VO → Model → Service → Facade 순으로 테스트를 쌓는다.

---

## 1. 테스트 피라미드 (이 프로젝트 기준)

```
        ┌───────────────┐
        │ E2E (Mock PG) │  ← 결제 통합 흐름 1~2개만
        └───────────────┘
       ┌─────────────────┐
       │ Facade 통합 테스트│  ← @SpringBootTest, 실 DB(testcontainers)
       └─────────────────┘
     ┌─────────────────────┐
     │ Service 단위 테스트   │  ← Fake Repository, 외부 의존 차단
     └─────────────────────┘
   ┌─────────────────────────┐
   │ Model / VO 단위 테스트     │  ← 순수 POJO, 의존 0
   └─────────────────────────┘
```

체크리스트의 "단위 테스트"는 **아래 두 단(Model/VO + Service)** 을 지칭한다.

---

## 2. 외부 의존성 분리 원칙

| 의존성 | 분리 방법 | 위치 |
|--------|----------|------|
| `XxxRepository` (도메인 인터페이스) | **Fake 구현체** (in-memory `HashMap`/`List`) | `src/test/java/.../domain/xxx/FakeXxxRepository.java` |
| `PasswordEncoder` (Spring Bean) | **Stub** (입력값 그대로 반환) | 테스트 클래스 내부 익명 객체 또는 별도 fixture |
| `PaymentGateway` (외부 PG 포트) | **Fake** (성공/실패 시나리오별 구현체) | `FakeSuccessPaymentGateway`, `FakeFailingPaymentGateway` |
| `Clock`/`LocalDateTime.now()` | 생성자 주입 가능한 `Clock`으로 변환 후 `Clock.fixed()` 사용 | — |

이미 존재하는 패턴: `apps/commerce-api/src/test/java/com/loopers/domain/user/InMemoryUserRepository.java` — 이 양식을 그대로 다른 도메인에 복제.

---

## 3. Like 도메인 — 단위 테스트 매트릭스

### 3-1. `LikeModelTest` (도메인 모델)

| # | 시나리오 | 기대 |
|---|---------|------|
| 1 | `LikeModel.of(userId, productId)` 생성 | `likedAt` 자동 세팅, `deletedAt` null |
| 2 | `markDeleted()` 호출 | `deletedAt` 세팅됨 |

### 3-2. `LikeServiceTest` (도메인 서비스, FakeRepository 사용)

| # | 시나리오 | 기대 |
|---|---------|------|
| 1 | `checkLikeExists` — 존재 X | `false` |
| 2 | `checkLikeExists` — 존재 O | `true` |
| 3 | `createLike` 정상 | `LikeModel` 저장, repository 호출 검증 |
| 4 | `findLikeModel` — 미존재 | `Optional.empty()` |
| 5 | `deleteLike` 정상 | repository에서 제거 |
| 6 | `findLikesByUserId` | 해당 유저 좋아요만 반환 |

### 3-3. `LikeFacadeTest` (멱등 정책, Fake LikeRepository + Fake ProductRepository)

| # | 시나리오 | 기대 |
|---|---------|------|
| 1 | `addLike` — 신규 등록 | LikeModel 생성, Product.likeCount +1, **201** 시맨틱 |
| 2 | `addLike` — 중복 (이미 존재) | likeCount 증분 없음, **200** 시맨틱 (no-op) |
| 3 | `removeLike` — 정상 취소 | LikeModel 삭제, Product.likeCount -1, **204** |
| 4 | `removeLike` — 미존재 | likeCount 감소 없음, **204** (no-op) |
| 5 | `removeLike` — likeCount가 0인 상태에서 호출 | 최솟값 0 유지, 음수 안 됨 |
| 6 | `findLikes` — 타인 userId 요청 | 권한 예외 (`FR-03.6`) |

> **중요:** 멱등성은 도메인 모델만으로는 보장 불가 — Facade가 "선제 exists 검사 → 분기"를 담당한다. DB 복합키 제약은 race condition 최후 안전망. 본 테스트가 정책 회귀를 잡는다.

---

## 4. Order 도메인 — 단위 테스트 매트릭스

### 4-1. `OrderModelTest` / `OrderItemModelTest` (도메인 모델)

| # | 시나리오 | 기대 |
|---|---------|------|
| 1 | `OrderModel.of(userId, items)` — items 1개 이상 | 정상 생성, `totalAmount` 계산 |
| 2 | `OrderModel.of(userId, [])` — 빈 items | 예외 (`R1`) |
| 3 | `OrderItemModel.totalPrice()` | `unitPriceSnapshot × quantity` |
| 4 | `Quantity(0)` 또는 음수 | 예외 (`R7`) |
| 5 | `Price(-1)` | 예외 |
| 6 | `OrderModel.isOwnedBy(userId)` | true/false 검증 |

### 4-2. `OrderServiceTest` (도메인 서비스, FakeOrderRepository)

| # | 시나리오 | 기대 |
|---|---------|------|
| 1 | `createOrder` 정상 | OrderModel 저장, PENDING 상태 |
| 2 | `getOrderModel` — 미존재 | `OrderNotFoundException` |
| 3 | `findOrders` — 날짜 범위 필터 | startAt ~ endAt 내만 반환 |
| 4 | `confirmOrder` | PENDING → CONFIRMED 전이 |
| 5 | `cancelOrder` | PENDING → CANCELLED 전이 |
| 6 | `confirmOrder` — 이미 CONFIRMED | 예외 (상태머신 위반) |

### 4-3. `OrderFacadeTest` (FakeProductRepository + FakeOrderRepository)

| # | 시나리오 | 기대 |
|---|---------|------|
| 1 | **정상 주문**: 재고 충분, 상품 존재 | 재고 차감, OrderModel(PENDING) 생성, 스냅샷 보존 |
| 2 | **예외**: 상품 부재 | `ProductNotFoundException`, 재고 변경 없음, Order 생성 안 됨 |
| 3 | **예외**: 재고 부족 | `InsufficientStockException`, **주문 전체 실패** (`R2`), 다른 상품 재고도 변경 없음 |
| 4 | **예외**: 재고 0인 상품 포함 | R9 — 재고 부족과 동일 처리 |
| 5 | **예외**: items 1개도 없는 요청 | `R1` 위반 예외 |
| 6 | **예외**: quantity = 0 | `R7` 위반 예외 |
| 7 | 스냅샷 보존: 주문 후 Product.name 변경 | OrderItem.productNameSnapshot 불변 |

> **롤백 검증 (#3):** Fake Repository 상태를 트랜잭션처럼 다루려면 in-memory 스냅샷/복원 헬퍼가 필요. 실제 트랜잭션 롤백은 통합 테스트(`@Transactional` + testcontainers)에서 검증. 단위 테스트는 "예외 발생 시 OrderRepository.save가 호출되지 않음"으로 대체 가능.

---

## 5. Fake 구현체 작성 규칙

```java
// 예: FakeLikeRepository
public class FakeLikeRepository implements LikeRepository {
    private final Map<LikeKey, LikeModel> store = new ConcurrentHashMap<>();

    @Override
    public LikeModel save(LikeModel model) {
        store.put(new LikeKey(model.getUserId(), model.getProductId()), model);
        return model;
    }
    // ... 인터페이스 메서드 그대로 구현
}
```

원칙:
- **인터페이스 100% 구현** — 일부 메서드만 구현하면 다른 테스트에서 NPE
- **상태 격리** — 각 테스트는 새 인스턴스 사용 (`@BeforeEach`에서 생성)
- **DB 제약 모사 금지** — 복합키 unique 등은 Facade의 선제 검사로 충분. 모사하려다 복잡도 증가하면 통합 테스트로 이관

---

## 6. 구현 순서 (바텀업)

```
[Phase 1: Domain 모델·VO]
  Stock VO 테스트 → reduceStock 음수 방지 검증
  Quantity VO 테스트
  Price VO 테스트
  LikeCount VO 테스트
  ↓
[Phase 2: Aggregate Root 메서드]
  ProductModel.reduceStock / incrementLikeCount / decrementLikeCount 테스트
  OrderModel.of / isOwnedBy 테스트
  LikeModel.of / markDeleted 테스트
  ↓
[Phase 3: Domain Service (Fake Repository)]
  ProductService 테스트 (validateAndReduceStock, restoreStock)
  OrderService 테스트
  LikeService 테스트
  ProductDetailService 테스트 ← 구현 전략 01 참조
  ↓
[Phase 4: Application Facade]
  OrderFacade 테스트 (정상/예외 7개 시나리오)
  LikeFacade 테스트 (멱등 6개 시나리오)
  ProductFacade 테스트 (도메인 서비스 위임 검증)
  ↓
[Phase 5: 통합·E2E]
  testcontainers 기반 통합 테스트
  ArchitectureTest 통과 확인
```

---

## 7. 커버리지 목표

| 레이어 | 라인 커버리지 | 분기 커버리지 |
|--------|--------------|--------------|
| VO / Model | 95%+ | 90%+ |
| Domain Service | 90%+ | 85%+ |
| Facade | 85%+ | 80%+ |
| Controller | 통합 테스트로 대체 | — |

> 숫자는 가이드라인. **시나리오 매트릭스(§3, §4) 전 항목 통과**가 더 중요하다.

---

## 8. 미결 사항

- 결제(Payment) 단위 테스트는 본 문서 범위 외 — `FakePaymentGateway` 패턴은 동일하게 적용. 후주차에서 별도 가이드.
- 동시성 테스트(재고 음수 방지의 동시 주문 시나리오)는 단위 테스트 범위 외 — 통합 테스트 또는 부하 테스트로 이관.