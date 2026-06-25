package com.loopers.application.product

import com.loopers.domain.product.Level
import com.loopers.domain.product.ProductDomain
import com.loopers.domain.product.ProductView
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

        /**
         * 합성: 읽기 모델(Product+Brand) + 실시간 likeCount/soldOut (ADR-0002 D0).
         */
        fun of(view: ProductView, likeCount: Int, soldOut: Boolean) =
            ProductInfo(
                productId = view.productId,
                name = view.name,
                author = view.author,
                category = view.category,
                level = view.level,
                price = view.price,
                likeCount = likeCount,
                brandId = view.brandId,
                brandName = view.brandName,
                soldOut = soldOut,
            )
    }

}
