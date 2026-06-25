package com.loopers.application.product

import com.loopers.domain.product.Level
import com.loopers.domain.product.ProductDomain
import com.loopers.domain.product.ProductModel
import com.loopers.domain.product.ProductStatus
import com.loopers.domain.product.TechCategory

data class AdminProductInfo private constructor (
    val productId: Long,
    val brandId: Long,
    val isbn: String,
    val name: String,
    val author: String,
    val category: TechCategory,
    val level: Level,
    val price: Double,
    val stockQuantity: Int,
    val likeCount: Int,
    val status: ProductStatus,
) {
    companion object {
        fun of(productDomain: ProductDomain) = AdminProductInfo(
            productId = productDomain.productId,
            brandId = productDomain.brandId,
            isbn = productDomain.isbn,
            name = productDomain.name,
            author = productDomain.author,
            category = productDomain.category,
            level = productDomain.level,
            price = productDomain.price,
            stockQuantity = productDomain.stockQuantity,
            likeCount = productDomain.likeCount,
            status = productDomain.status,
        )

        fun of(productModel: ProductModel, stockQuantity: Int, likeCount: Int = 0) = AdminProductInfo(
            productId = productModel.id,
            brandId = productModel.brandId,
            isbn = productModel.isbn,
            name = productModel.name,
            author = productModel.authName,
            category = productModel.techCategory,
            level = productModel.level,
            price = productModel.price,
            stockQuantity = stockQuantity,
            likeCount = likeCount,
            status = productModel.status,
        )
    }
}
