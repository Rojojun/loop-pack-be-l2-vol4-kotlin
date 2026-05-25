package com.loopers.application.order

import java.time.LocalDateTime

data class AdminOrderSummaryInfo(
    val orderId: Long,
    val userId: Long,
    val totalAmount: Int,
    val status: String,
    val orderedAt: LocalDateTime,
    val itemCount: Int,
)

data class AdminOrderDetailInfo(
    val orderId: Long,
    val userId: Long,
    val totalAmount: Int,
    val status: String,
    val orderedAt: LocalDateTime,
    val items: List<OrderItemInfo>,
)