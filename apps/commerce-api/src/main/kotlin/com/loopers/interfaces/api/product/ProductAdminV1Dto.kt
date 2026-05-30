package com.loopers.interfaces.api.product

import com.loopers.application.product.AdminProductInfo
import com.loopers.domain.product.ProductStatus as DomainProductStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class ProductAdminV1Dto {
    data class CreateProductRequest(
        @field:NotNull val brandId: Long,
        @field:NotBlank val isbn: String,
        @field:NotBlank val name: String,
        @field:NotBlank val author: String,
        @field:NotNull val category: ProductV1Dto.TechCategory,
        @field:NotNull val level: ProductV1Dto.Level,
        @field:Min(0) val price: Double,
        @field:Min(0) val initialQuantity: Int,
        val description: String = "",
    )

    data class UpdateProductRequest(
        @field:NotBlank val name: String,
        @field:NotBlank val author: String,
        @field:NotNull val category: ProductV1Dto.TechCategory,
        @field:NotNull val level: ProductV1Dto.Level,
        @field:Min(0) val price: Double,
    )

    enum class ProductStatus {
        ACTIVE,
        DELETED;

        companion object {
            fun from(domain: DomainProductStatus): ProductStatus = when (domain) {
                DomainProductStatus.ACTIVE -> ACTIVE
                DomainProductStatus.DELETED -> DELETED
            }
        }
    }

    data class ProductAdminResponse(
        val productId: Long,
        val brandId: Long,
        val isbn: String,
        val name: String,
        val author: String,
        val category: ProductV1Dto.TechCategory,
        val level: ProductV1Dto.Level,
        val price: Int,
        val stockQuantity: Int,
        val likeCount: Int,
        val status: ProductStatus,
    ) {
        companion object {
            fun from(info: AdminProductInfo): ProductAdminResponse = ProductAdminResponse(
                productId = info.productId,
                brandId = info.brandId,
                isbn = info.isbn,
                name = info.name,
                author = info.author,
                category = ProductV1Dto.TechCategory.from(info.category),
                level = ProductV1Dto.Level.from(info.level),
                price = info.price.toInt(),
                stockQuantity = info.stockQuantity,
                likeCount = info.likeCount,
                status = ProductStatus.from(info.status),
            )
        }
    }
}
