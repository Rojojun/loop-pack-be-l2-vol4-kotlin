package com.loopers.application.order

import com.loopers.domain.order.OrderService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class AdminOrderFacade(
    private val orderService: OrderService
) {
    fun findAllOrders(pageable: Pageable): Page<AdminOrderSummaryInfo> {
        return orderService.findAll(pageable)
            .map { AdminOrderSummaryInfo(
                orderId = it.id,
                userId = it.userId,
                totalPrice = it.totalPrice(),
                status = it.status,
                orderedAt = it.orderedAt,
                itemCount = it.items.size,
            ) }
    }

    fun getOrder(orderId: Long): AdminOrderDetailInfo {
        val order = orderService.getOrder(orderId)
        val orderItemInfos = order.items.map { OrderItemInfo(
            productId = it.productId,
            productNameSnapshot = it.productName,
            unitPriceSnapshot = it.unitPrice,
            quantity = it.quantity,
            totalPrice = it.totalPrice(),
        ) }
        return AdminOrderDetailInfo(
            orderId = order.id,
            userId = order.userId,
            totalPrice = order.totalPrice(),
            status = order.status,
            orderedAt = order.orderedAt,
            items = orderItemInfos
        )
    }
}
