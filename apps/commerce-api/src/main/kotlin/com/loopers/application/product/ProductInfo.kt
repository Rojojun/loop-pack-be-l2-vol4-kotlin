package com.loopers.application.product

import com.loopers.domain.product.Level
import com.loopers.domain.product.ProductDomain
import com.loopers.domain.product.TechCategory

data class ProductInfo private constructor (
    val productId: Long,
    val name: String,
    val author: String,
    val category: TechCategory,
    val level: Level,
    val price: Double,
    val likeCount: Int,
    val brandId: Long,
    val brandName: String,
    val soldOut: Boolean,
) {
    companion object {
        fun of(productDomain: ProductDomain) =
            ProductInfo(
                productId = productDomain.productId,
                name = productDomain.name,
                author = productDomain.author,
                category = productDomain.category,
                level = productDomain.level,
                price = productDomain.price,
                likeCount = productDomain.likeCount,
                brandId = productDomain.brandId,
                brandName = productDomain.brandName ?: "unknown",
                soldOut = productDomain.soldOut,
            )
    }

}
