package com.loopers.application.order

import com.loopers.domain.order.OrderStatus
import java.time.ZonedDateTime

data class OrderInfo(
    val orderId: Long,
)

data class OrderSummaryInfo(
    val orderId: Long,
    val totalPrice: Double,
    val status: OrderStatus,
    val orderedAt: ZonedDateTime,
    val itemCount: Int,
)

data class OrderDetailInfo(
    val orderId: Long,
    val totalPrice: Double,
    val discountAmount: Double,
    val finalAmount: Double,
    val status: OrderStatus,
    val orderedAt: ZonedDateTime,
    val items: List<OrderItemInfo>,
)

data class OrderItemInfo(
    val productId: Long,
    val productNameSnapshot: String,
    val unitPriceSnapshot: Double,
    val quantity: Int,
    val totalPrice: Double,
)
