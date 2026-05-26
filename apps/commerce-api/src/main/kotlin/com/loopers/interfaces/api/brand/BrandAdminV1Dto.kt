package com.loopers.interfaces.api.brand

import com.loopers.application.brand.AdminBrandInfo
import com.loopers.domain.brand.BrandStatus
import jakarta.validation.constraints.NotBlank

class BrandAdminV1Dto {
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
        val status: BrandStatus,
    ) {
        companion object {
            fun from(info: AdminBrandInfo): BrandAdminResponse =
                BrandAdminResponse(
                    id = info.id,
                    name = info.name,
                    status = info.status,
                )
        }
    }
}
