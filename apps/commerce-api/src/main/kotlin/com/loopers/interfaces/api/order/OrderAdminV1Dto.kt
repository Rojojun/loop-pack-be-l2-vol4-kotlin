package com.loopers.interfaces.api.order

import com.loopers.application.order.AdminOrderDetailInfo
import com.loopers.application.order.AdminOrderSummaryInfo
import java.time.ZonedDateTime

class OrderAdminV1Dto {
    data class OrderAdminSummaryResponse(
        val orderId: Long,
        val userId: Long,
        val totalPrice: Double,
        val status: OrderV1Dto.OrderStatus,
        val orderedAt: ZonedDateTime,
        val itemCount: Int,
    ) {
        companion object {
            fun from(info: AdminOrderSummaryInfo): OrderAdminSummaryResponse =
                OrderAdminSummaryResponse(
                    orderId = info.orderId,
                    userId = info.userId,
                    totalPrice = info.totalPrice,
                    status = OrderV1Dto.OrderStatus.from(info.status),
                    orderedAt = info.orderedAt,
                    itemCount = info.itemCount,
                )
        }
    }

    data class OrderAdminDetailResponse(
        val orderId: Long,
        val userId: Long,
        val totalPrice: Double,
        val status: OrderV1Dto.OrderStatus,
        val orderedAt: ZonedDateTime,
        val items: List<OrderV1Dto.OrderItemDetailResponse>,
    ) {
        companion object {
            fun from(info: AdminOrderDetailInfo): OrderAdminDetailResponse =
                OrderAdminDetailResponse(
                    orderId = info.orderId,
                    userId = info.userId,
                    totalPrice = info.totalPrice,
                    status = OrderV1Dto.OrderStatus.from(info.status),
                    orderedAt = info.orderedAt,
                    items = info.items.map { OrderV1Dto.OrderItemDetailResponse.from(it) }
                )
        }
    }
}
