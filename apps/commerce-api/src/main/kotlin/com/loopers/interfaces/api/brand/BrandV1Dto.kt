package com.loopers.interfaces.api.brand

import com.loopers.application.brand.BrandInfo

class BrandV1Dto {
    // TODO: Response 필드를 docs/design/01a-api-spec.md 참고하여 채우세요.
    data class BrandResponse(
        val id: Long,
        val name: String,
    ) {
        companion object {
            fun from(info: BrandInfo): BrandResponse = TODO("BrandInfo → BrandResponse 매핑")
        }
    }
}