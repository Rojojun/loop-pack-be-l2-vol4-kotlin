package com.loopers.interfaces.api.order

import com.loopers.application.order.OrderDetailInfo
import com.loopers.application.order.OrderInfo
import com.loopers.application.order.OrderSummaryInfo
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

class OrderV1Dto {
    // TODO: Request/Response 필드를 docs/design/01a-api-spec.md 참고하여 채우세요.

    data class PlaceOrderRequest(
        @field:NotEmpty
        @field:Valid
        val items: List<OrderItemRequest>,
    )

    data class OrderItemRequest(
        @field:NotNull val productId: Long,
        @field:Min(1) val quantity: Int,
    )

    data class OrderResponse(
        val orderId: Long,
    ) {
        companion object {
            fun from(info: OrderInfo): OrderResponse = TODO("OrderInfo → OrderResponse 매핑")
        }
    }

    data class OrderSummaryResponse(
        val orderId: Long,
        val totalAmount: Int,
        val status: String,
        val orderedAt: LocalDateTime,
        val itemCount: Int,
    ) {
        companion object {
            fun from(info: OrderSummaryInfo): OrderSummaryResponse =
                TODO("OrderSummaryInfo → OrderSummaryResponse 매핑")
        }
    }

    data class OrderDetailResponse(
        val orderId: Long,
        val totalAmount: Int,
        val status: String,
        val orderedAt: LocalDateTime,
        val items: List<OrderItemDetailResponse>,
    ) {
        companion object {
            fun from(info: OrderDetailInfo): OrderDetailResponse =
                TODO("OrderDetailInfo → OrderDetailResponse 매핑")
        }
    }

    data class OrderItemDetailResponse(
        val productId: Long,
        val productNameSnapshot: String,
        val unitPriceSnapshot: Int,
        val quantity: Int,
        val totalPrice: Int,
    )
}
