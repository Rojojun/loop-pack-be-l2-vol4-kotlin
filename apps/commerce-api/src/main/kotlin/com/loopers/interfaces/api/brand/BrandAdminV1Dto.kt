package com.loopers.interfaces.api.brand

import com.loopers.application.brand.AdminBrandInfo
import com.loopers.domain.brand.BrandStatus as DomainBrandStatus
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

    enum class BrandStatus {
        ACTIVE,
        CLOSED,
        DELETED;

        companion object {
            fun from(domain: DomainBrandStatus): BrandStatus = when (domain) {
                DomainBrandStatus.ACTIVE -> ACTIVE
                DomainBrandStatus.CLOSED -> CLOSED
                DomainBrandStatus.DELETED -> DELETED
            }
        }
    }

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
                    status = BrandStatus.from(info.status),
                )
        }
    }
}
