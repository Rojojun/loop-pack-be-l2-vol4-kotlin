package com.loopers.application.order

import java.time.LocalDateTime

data class OrderInfo(
    // 주문 생성 결과 — 보통 orderId만 반환하거나 간단 요약
    val orderId: Long,
)

data class OrderSummaryInfo(
    val orderId: Long,
    val totalAmount: Int,
    val status: String,
    val orderedAt: LocalDateTime,
    val itemCount: Int,
)

data class OrderDetailInfo(
    val orderId: Long,
    val totalAmount: Int,
    val status: String,
    val orderedAt: LocalDateTime,
    val items: List<OrderItemInfo>,
)

data class OrderItemInfo(
    val productId: Long,
    val productNameSnapshot: String,
    val unitPriceSnapshot: Int,
    val quantity: Int,
    val totalPrice: Int,
)