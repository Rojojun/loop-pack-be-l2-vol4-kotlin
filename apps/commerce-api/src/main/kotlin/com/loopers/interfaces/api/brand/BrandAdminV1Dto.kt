package com.loopers.interfaces.api.brand

import com.loopers.application.brand.AdminBrandInfo
import jakarta.validation.constraints.NotBlank

class BrandAdminV1Dto {
    // TODO: Request/Response 필드를 docs/design/01a-api-spec.md 참고하여 채우세요.

    data class CreateBrandRequest(
        @field:NotBlank
        val name: String,
    )

    data class UpdateBrandRequest(
        @field:NotBlank
        val name: String,
    )

    data class BrandAdminResponse(
        val id: Long,
        val name: String,
        val status: String,
    ) {
        companion object {
            fun from(info: AdminBrandInfo): BrandAdminResponse = TODO("AdminBrandInfo → BrandAdminResponse 매핑")
        }
    }
}
