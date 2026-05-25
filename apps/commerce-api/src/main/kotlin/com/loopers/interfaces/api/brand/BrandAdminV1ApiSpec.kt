package com.loopers.interfaces.api.brand

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "Brand Admin V1 API", description = "Brand 도메인 어드민 API 입니다.")
interface BrandAdminV1ApiSpec {
    @Operation(summary = "브랜드 목록 조회 (어드민)", description = "페이지 단위로 브랜드 목록을 조회합니다.")
    fun getBrands(pageable: Pageable): ApiResponse<Page<BrandAdminV1Dto.BrandAdminResponse>>

    @Operation(summary = "브랜드 상세 조회 (어드민)", description = "ID로 브랜드 정보를 조회합니다.")
    fun getBrand(
        @Schema(name = "브랜드 ID", description = "조회할 브랜드의 ID") @PathVariable brandId: Long,
    ): ApiResponse<BrandAdminV1Dto.BrandAdminResponse>

    @Operation(summary = "브랜드 등록 (어드민)", description = "새 브랜드를 등록합니다.")
    fun createBrand(
        @Schema(name = "브랜드 등록 DTO") @RequestBody @Valid request: BrandAdminV1Dto.CreateBrandRequest,
    ): ApiResponse<BrandAdminV1Dto.BrandAdminResponse>

    @Operation(summary = "브랜드 수정 (어드민)", description = "ID로 브랜드를 수정합니다.")
    fun updateBrand(
        @Schema(name = "브랜드 ID") @PathVariable brandId: Long,
        @Schema(name = "브랜드 수정 DTO") @RequestBody @Valid request: BrandAdminV1Dto.UpdateBrandRequest,
    ): ApiResponse<BrandAdminV1Dto.BrandAdminResponse>

    @Operation(summary = "브랜드 삭제 (어드민)", description = "ID로 브랜드를 소프트 삭제하고 소속 상품을 cascade로 처리합니다.")
    fun deleteBrand(
        @Schema(name = "브랜드 ID") @PathVariable brandId: Long,
    ): ApiResponse<Any>
}
