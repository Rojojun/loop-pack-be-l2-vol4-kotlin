package com.loopers.interfaces.api.product

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Product Admin V1 API", description = "Product 도메인 어드민 API 입니다.")
interface ProductAdminV1ApiSpec {
    @Operation(summary = "상품 목록 조회 (어드민)", description = "전체 상품 또는 브랜드별 상품을 페이지 단위로 조회합니다.")
    fun findProducts(
        @Schema(name = "브랜드 ID", description = "특정 브랜드 필터") @RequestParam(required = false) brandId: Long?,
        pageable: Pageable,
    ): ApiResponse<Page<ProductAdminV1Dto.ProductAdminResponse>>

    @Operation(summary = "상품 상세 조회 (어드민)", description = "ID로 상품을 조회합니다.")
    fun getProduct(
        @Schema(name = "상품 ID") @PathVariable productId: Long,
    ): ApiResponse<ProductAdminV1Dto.ProductAdminResponse>

    @Operation(summary = "상품 등록 (어드민)", description = "Product + Stock을 함께 등록합니다.")
    fun createProduct(
        @Schema(name = "상품 등록 DTO") @RequestBody @Valid request: ProductAdminV1Dto.CreateProductRequest,
    ): ApiResponse<ProductAdminV1Dto.ProductAdminResponse>

    @Operation(summary = "상품 수정 (어드민)", description = "ID로 상품을 수정합니다. 브랜드는 수정 불가.")
    fun updateProduct(
        @Schema(name = "상품 ID") @PathVariable productId: Long,
        @Schema(name = "상품 수정 DTO") @RequestBody @Valid request: ProductAdminV1Dto.UpdateProductRequest,
    ): ApiResponse<ProductAdminV1Dto.ProductAdminResponse>

    @Operation(summary = "상품 삭제 (어드민)", description = "ID로 상품을 소프트 삭제합니다.")
    fun deleteProduct(
        @Schema(name = "상품 ID") @PathVariable productId: Long,
    ): ApiResponse<Any>
}
