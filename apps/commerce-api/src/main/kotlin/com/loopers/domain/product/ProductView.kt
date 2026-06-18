package com.loopers.domain.product

import com.loopers.domain.brand.BrandModel

/**
 * 상품 리스트 표시용 읽기 모델 (CQRS read side).
 *
 * 변동성이 낮은 Product + Brand 데이터만 담는다.
 * - 재고/soldOut(Stock), likeCount 는 의도적으로 제외 → 조회 시점에 실시간 합성한다 (ADR-0002 D0).
 * - price 는 "표시용 정가". 결제 최종가(할인 적용가)는 캐시 금지 (D0).
 */
data class ProductView(
    val productId: Long,
    val name: String,
    val author: String,
    val category: TechCategory,
    val level: Level,
    val price: Double,
    val brandId: Long,
    val brandName: String,
) {
    companion object {
        fun of(product: ProductModel, brand: BrandModel): ProductView =
            ProductView(
                productId = product.id,
                name = product.name,
                author = product.authName,
                category = product.techCategory,
                level = product.level,
                price = product.price,
                brandId = product.brandId,
                brandName = brand.name,
            )
    }
}
