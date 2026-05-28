package com.loopers.interfaces.api.order

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import java.time.ZonedDateTime

@Tag(name = "Order V1 API", description = "Order 도메인 대고객 API 입니다.")
interface OrderV1ApiSpec {
    @Operation(summary = "주문 요청", description = "여러 상품 + 수량으로 주문을 생성합니다.")
    fun placeOrder(
        @Schema(name = "로그인 ID") @RequestHeader("X-Loopers-LoginId") loginId: String,
        @Schema(name = "주문 요청 DTO") @RequestBody @Valid request: OrderV1Dto.PlaceOrderRequest,
    ): ApiResponse<OrderV1Dto.OrderResponse>

    @Operation(summary = "주문 목록 조회", description = "날짜 범위(최대 365일)로 본인 주문을 조회합니다.")
    fun findOrders(
        @Schema(name = "로그인 ID") @RequestHeader("X-Loopers-LoginId") loginId: String,
        @Schema(name = "조회 시작일") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startAt: ZonedDateTime,
        @Schema(name = "조회 종료일") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endAt: ZonedDateTime,
    ): ApiResponse<List<OrderV1Dto.OrderSummaryResponse>>

    @Operation(summary = "주문 상세 조회", description = "ID로 본인 주문 상세를 조회합니다.")
    fun getOrder(
        @Schema(name = "로그인 ID") @RequestHeader("X-Loopers-LoginId") loginId: String,
        @Schema(name = "주문 ID") @PathVariable orderId: Long,
    ): ApiResponse<OrderV1Dto.OrderDetailResponse>
}
