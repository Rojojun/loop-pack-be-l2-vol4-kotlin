package com.loopers.interfaces.api.product

import com.loopers.application.product.AdminProductInfo
import com.loopers.domain.product.Level
import com.loopers.domain.product.TechCategory
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class ProductAdminV1Dto {
    data class CreateProductRequest(
        @field:NotNull val brandId: Long,
        @field:NotBlank val isbn: String,
        @field:NotBlank val name: String,
        @field:NotBlank val author: String,
        @field:NotBlank val category: TechCategory,
        @field:NotBlank val level: Level,
        @field:Min(0) val price: Double,
        @field:Min(0) val initialQuantity: Int,
        val description: String = "",
    )

    data class UpdateProductRequest(
        @field:NotBlank val name: String,
        @field:NotBlank val author: String,
        @field:NotBlank val category: TechCategory,
        @field:NotBlank val level: Level,
        @field:Min(0) val price: Double,
    )

    data class ProductAdminResponse(
        val productId: Long,
        val brandId: Long,
        val isbn: String,
        val name: String,
        val author: String,
        val category: String,
        val level: String,
        val price: Int,
        val stockQuantity: Int,
        val likeCount: Int,
        val status: String,
    ) {
        companion object {
            fun from(info: AdminProductInfo): ProductAdminResponse = ProductAdminResponse(
                productId = info.productId,
                brandId = info.brandId,
                isbn = info.isbn,
                name = info.name,
                author = info.author,
                category = info.category.name,
                level = info.level.name,
                price = info.price.toInt(),
                stockQuantity = info.stockQuantity,
                likeCount = info.likeCount,
                status = info.status.name,
            )
        }
    }
}
