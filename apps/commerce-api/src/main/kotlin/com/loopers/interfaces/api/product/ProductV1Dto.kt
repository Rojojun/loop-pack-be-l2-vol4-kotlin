package com.loopers.interfaces.api.product

import com.loopers.application.product.ProductInfo

class ProductV1Dto {
    // TODO: Response 필드를 docs/design/01a-api-spec.md §대고객 노출 정보 참고하여 채우세요.
    // (id, name, author, category, level, price, likeCount, brandId, brandName, soldOut)
    // stock 수량·status·isbn은 대고객 노출 금지

    data class ProductResponse(
        val productId: Long,
        val name: String,
        val author: String,
        val category: String,
        val level: String,
        val price: Int,
        val likeCount: Int,
        val brandId: Long,
        val brandName: String,
        val soldOut: Boolean,
    ) {
        companion object {
            fun from(info: ProductInfo): ProductResponse = TODO("ProductInfo → ProductResponse 매핑")
        }
    }
}
