package com.loopers.interfaces.api.product

import com.loopers.application.product.ProductInfo

class ProductV1Dto {
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
            fun from(info: ProductInfo): ProductResponse = ProductResponse(
                productId = info.productId,
                name = info.name,
                author = info.author,
                category = info.category.name,
                level = info.level.name,
                price = info.price.toInt(),
                likeCount = info.likeCount,
                brandId = info.brandId,
                brandName = info.brandName,
                soldOut = info.soldOut,
            )
        }
    }
}
