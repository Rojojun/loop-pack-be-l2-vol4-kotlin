package com.loopers.interfaces.api.order

import com.loopers.application.order.OrderFacade
import com.loopers.interfaces.api.ApiResponse
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.ZonedDateTime

@RestController
@RequestMapping("/api/v1/orders")
class OrderV1ApiController(
    private val orderFacade: OrderFacade,
) : OrderV1ApiSpec {

    @PostMapping
    override fun placeOrder(
        @RequestHeader("X-Loopers-LoginId") loginId: String,
        @RequestBody @Valid request: OrderV1Dto.PlaceOrderRequest,
    ): ApiResponse<OrderV1Dto.OrderResponse> =
        request.items
            .map { it.productId to it.quantity }
            .let { orderFacade.placeOrder(loginId, it) }
            .let { OrderV1Dto.OrderResponse.from(it) }
            .let { ApiResponse.success(it) }

    @GetMapping
    override fun findOrders(
        @RequestHeader("X-Loopers-LoginId") loginId: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startAt: ZonedDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endAt: ZonedDateTime,
    ): ApiResponse<List<OrderV1Dto.OrderSummaryResponse>> {
        val list = orderFacade.findOrders(loginId, startAt, endAt)
            .map { OrderV1Dto.OrderSummaryResponse.from(it) }
        return ApiResponse.success(list)
    }

    @GetMapping("/{orderId}")
    override fun getOrder(
        @RequestHeader("X-Loopers-LoginId") loginId: String,
        @PathVariable orderId: Long,
    ): ApiResponse<OrderV1Dto.OrderDetailResponse> {
        val info = orderFacade.getOrder(loginId, orderId)
        val response = OrderV1Dto.OrderDetailResponse.from(info)
        return ApiResponse.success(response)
    }
}
