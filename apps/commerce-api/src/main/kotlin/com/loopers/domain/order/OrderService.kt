package com.loopers.domain.order

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class OrderService(
    private val orderRepository: OrderRepository
) {
    fun createOrder(userId: Long, items: List<OrderItemModel>): OrderModel =
        OrderModel.of(userId, items)
            .let { orderRepository.save(it) }
}
