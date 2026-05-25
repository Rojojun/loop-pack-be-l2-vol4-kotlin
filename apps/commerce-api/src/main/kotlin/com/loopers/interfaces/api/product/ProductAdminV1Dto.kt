package com.loopers.interfaces.api.product

import com.loopers.application.product.AdminProductInfo
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class ProductAdminV1Dto {
    // TODO: Request/Response 필드를 docs/design/01a-api-spec.md 참고하여 채우세요.

    data class CreateProductRequest(
        @field:NotNull val brandId: Long,
        @field:NotBlank val isbn: String,
        @field:NotBlank val name: String,
        @field:NotBlank val author: String,
        @field:NotBlank val category: String,
        @field:NotBlank val level: String,
        @field:Min(0) val price: Int,
        @field:Min(0) val initialQuantity: Int,
    )

    data class UpdateProductRequest(
        @field:NotBlank val name: String,
        @field:NotBlank val author: String,
        @field:NotBlank val category: String,
        @field:NotBlank val level: String,
        @field:Min(0) val price: Int,
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
            fun from(info: AdminProductInfo): ProductAdminResponse = TODO("AdminProductInfo → ProductAdminResponse 매핑")
        }
    }
}