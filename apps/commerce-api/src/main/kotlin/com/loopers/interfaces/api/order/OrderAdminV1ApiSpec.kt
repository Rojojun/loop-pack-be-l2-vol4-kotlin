package com.loopers.interfaces.api.order

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "Order Admin V1 API", description = "Order 도메인 어드민 API 입니다.")
interface OrderAdminV1ApiSpec {
    @Operation(summary = "전체 주문 목록 조회 (어드민)", description = "페이지 단위로 전체 주문을 조회합니다.")
    fun findAllOrders(pageable: Pageable): ApiResponse<Page<OrderAdminV1Dto.OrderAdminSummaryResponse>>

    @Operation(summary = "주문 상세 조회 (어드민)", description = "ID로 주문 상세를 조회합니다.")
    fun getOrder(
        @Schema(name = "주문 ID") @PathVariable orderId: Long,
    ): ApiResponse<OrderAdminV1Dto.OrderAdminDetailResponse>
}
