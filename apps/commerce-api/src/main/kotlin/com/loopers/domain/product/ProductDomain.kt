package com.loopers.domain.product

import com.loopers.domain.brand.BrandModel
import com.loopers.domain.stock.StockModel

data class ProductDomain private constructor(
    val productId: Long,
    val brandId: Long,
    val brandName: String? = null,
    val isbn: String,
    val name: String,
    val author: String,
    val category: TechCategory,
    val level: Level,
    val price: Double,
    val stockQuantity: Int,
    val likeCount: Int = 0,
    val status: ProductStatus,
    val soldOut: Boolean = false,
) {
    companion object {
        fun ofAdmin(productModel: ProductModel, stockQuantity: Int, likeCount: Int): ProductDomain =
            ProductDomain(
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

        fun ofUser(productModel: ProductModel, brandModel: BrandModel, likeCount: Int, stockModel: StockModel): ProductDomain =
            ProductDomain(
                productId = productModel.id,
                brandId = brandModel.id,
                brandName = brandModel.name,
                isbn = productModel.isbn,
                name = productModel.name,
                author = productModel.authName,
                category = productModel.techCategory,
                level = productModel.level,
                price = productModel.price,
                stockQuantity = stockModel.quantity,
                likeCount = likeCount,
                status = productModel.status,
                soldOut = stockModel.isSoldOut()
            )
    }
}
