package com.loopers.interfaces.api.brand

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "Brand V1 API", description = "Brand 도메인 대고객 API 입니다.")
interface BrandV1ApiSpec {
    @Operation(summary = "브랜드 조회", description = "ID로 브랜드 정보를 조회합니다.")
    fun getBrand(
        @Schema(name = "브랜드 ID", description = "조회할 브랜드의 ID") @PathVariable brandId: Long,
    ): ApiResponse<BrandV1Dto.BrandResponse>
}