package com.loopers.interfaces.api.coupon

import com.loopers.interfaces.api.ApiResponse
import com.loopers.interfaces.api.PageResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "Coupon Admin V1 API", description = "Coupon 도메인 어드민 API 입니다.")
interface CouponAdminV1ApiSpec {
    @Operation(summary = "쿠폰 템플릿 목록 조회", description = "페이지 단위로 쿠폰의 템플릿 목록을 조회합니다.")
    fun getCoupons(pageable: Pageable): ApiResponse<PageResponse<CouponAdminV1Dto.CouponResponse>>

    @Operation(summary = "쿠폰 템플릿 상세 조회", description = "ID로 쿠폰 정보를 조회합니다.")
    fun getCoupon(
        @Schema(name = "쿠폰 ID", description = "조회할 쿠폰의 ID") @PathVariable couponId: Long,
    ): ApiResponse<CouponAdminV1Dto.CouponResponse>

    @Operation(summary = "쿠폰 템플릿 등록", description = "정액(FIXED) / 정률(RATE) 타입 지정 후 새 쿠폰 템플릿 등록합니다.")
    fun createCoupon(
        @Schema(name = "쿠폰 등록 DTO") @RequestBody @Valid request: CouponAdminV1Dto.CreateCouponRequest,
    ): ApiResponse<CouponAdminV1Dto.CouponResponse>

    @Operation(summary = "쿠폰 수정", description = "ID로 쿠폰 템플릿을 수정합니다.")
    fun updateCoupon(
        @Schema(name = "쿠폰 ID") @PathVariable couponId: Long,
        @Schema(name = "쿠폰 수정 DTO") @RequestBody @Valid request: CouponAdminV1Dto.UpdateCouponRequest,
    ): ApiResponse<CouponAdminV1Dto.CouponResponse>

    @Operation(summary = "쿠폰 삭제", description = "ID로 쿠폰 템플릿을 삭제합니다.")
    fun deleteCoupon(
        @Schema(name = "쿠폰 ID") @PathVariable couponId: Long,
    ): ApiResponse<Any>

    @Operation(summary = "쿠폰 발급 내역 조회", description = "ID로 특정 쿠폰의 발급 내역을 페이지 단위로 조회합니다.")
    fun getCouponHistory(
        @Schema(name = "쿠폰 ID") @PathVariable couponId: Long,
        pageable: Pageable,
    ): ApiResponse<PageResponse<CouponAdminV1Dto.CouponHistoryResponse>>
}
