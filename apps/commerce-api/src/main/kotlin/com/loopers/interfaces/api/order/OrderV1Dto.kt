package com.loopers.interfaces.api.order

import com.loopers.application.order.OrderDetailInfo
import com.loopers.application.order.OrderInfo
import com.loopers.application.order.OrderItemInfo
import com.loopers.application.order.OrderSummaryInfo
import com.loopers.domain.order.OrderStatus as DomainOrderStatus
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.ZonedDateTime

class OrderV1Dto {

    enum class OrderStatus {
        PENDING,
        CONFIRMED,
        CANCELLED;

        companion object {
            fun from(domain: DomainOrderStatus): OrderStatus = when (domain) {
                DomainOrderStatus.PENDING -> PENDING
                DomainOrderStatus.CONFIRMED -> CONFIRMED
                DomainOrderStatus.CANCELLED -> CANCELLED
            }
        }
    }

    data class PlaceOrderRequest(
        val couponId:Long? = null,
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
            fun from(info: OrderInfo): OrderResponse = OrderResponse(info.orderId)
        }
    }

    data class OrderSummaryResponse(
        val orderId: Long,
        val totalPrice: Double,
        val status: OrderStatus,
        val orderedAt: ZonedDateTime,
        val itemCount: Int,
    ) {
        companion object {
            fun from(info: OrderSummaryInfo): OrderSummaryResponse =
                OrderSummaryResponse(
                    orderId = info.orderId,
                    totalPrice = info.totalPrice,
                    status = OrderStatus.from(info.status),
                    orderedAt = info.orderedAt,
                    itemCount = info.itemCount,
                )
        }
    }

    data class OrderDetailResponse(
        val orderId: Long,
        val totalPrice: Double,
        val discountAmount: Double,
        val finalAmount: Double,
        val status: OrderStatus,
        val orderedAt: ZonedDateTime,
        val items: List<OrderItemDetailResponse>,
    ) {
        companion object {
            fun from(info: OrderDetailInfo): OrderDetailResponse =
                OrderDetailResponse(
                    orderId = info.orderId,
                    totalPrice = info.totalPrice,
                    discountAmount = info.discountAmount,
                    finalAmount = info.finalAmount,
                    status = OrderStatus.from(info.status),
                    orderedAt = info.orderedAt,
                    items = info.items.map { OrderItemDetailResponse.from(it) }
                )
        }
    }

    data class OrderItemDetailResponse(
        val productId: Long,
        val productNameSnapshot: String,
        val unitPriceSnapshot: Double,
        val quantity: Int,
        val totalPrice: Double,
    ) {
        companion object {
            fun from(info: OrderItemInfo) =
                OrderItemDetailResponse(
                    productId = info.productId,
                    productNameSnapshot = info.productNameSnapshot,
                    unitPriceSnapshot = info.unitPriceSnapshot,
                    quantity = info.quantity,
                    totalPrice = info.totalPrice,
                )
        }
    }
}
