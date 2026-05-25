package com.loopers.interfaces.api.order

import com.loopers.application.order.AdminOrderDetailInfo
import com.loopers.application.order.AdminOrderSummaryInfo
import java.time.LocalDateTime

class OrderAdminV1Dto {
    // TODO: 어드민 응답 필드 — userId 포함 등

    data class OrderAdminSummaryResponse(
        val orderId: Long,
        val userId: Long,
        val totalAmount: Int,
        val status: String,
        val orderedAt: LocalDateTime,
        val itemCount: Int,
    ) {
        companion object {
            fun from(info: AdminOrderSummaryInfo): OrderAdminSummaryResponse =
                TODO("AdminOrderSummaryInfo → OrderAdminSummaryResponse 매핑")
        }
    }

    data class OrderAdminDetailResponse(
        val orderId: Long,
        val userId: Long,
        val totalAmount: Int,
        val status: String,
        val orderedAt: LocalDateTime,
        val items: List<OrderV1Dto.OrderItemDetailResponse>,
    ) {
        companion object {
            fun from(info: AdminOrderDetailInfo): OrderAdminDetailResponse =
                TODO("AdminOrderDetailInfo → OrderAdminDetailResponse 매핑")
        }
    }
}