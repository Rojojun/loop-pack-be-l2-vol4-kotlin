package com.loopers.application.order

import com.loopers.domain.order.OrderStatus
import java.time.ZonedDateTime

data class AdminOrderSummaryInfo(
    val orderId: Long,
    val userId: Long,
    val totalPrice: Double,
    val status: OrderStatus,
    val orderedAt: ZonedDateTime,
    val itemCount: Int,
)

data class AdminOrderDetailInfo(
    val orderId: Long,
    val userId: Long,
    val totalPrice: Double,
    val status: OrderStatus,
    val orderedAt: ZonedDateTime,
    val items: List<OrderItemInfo>,
)
