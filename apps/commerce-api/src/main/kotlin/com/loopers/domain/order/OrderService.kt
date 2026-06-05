package com.loopers.domain.order

import com.loopers.support.function.orThrowNotFound
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Transactional
@Component
class OrderService(
    private val orderRepository: OrderRepository
) {
    fun createOrder(userId: Long, items: List<OrderItemModel>): OrderModel =
        OrderModel.of(userId, items)
            .let { orderRepository.save(it) }

    fun deleteOrder(orderId: Long, userId: Long) =
        orderRepository.findByIdOrNull(orderId)
            .orThrowNotFound("해당하는 주문이 존재하지 않습니다.")
            .cancel(userId)

    fun getOrders(startAt: ZonedDateTime, endAt: ZonedDateTime) =
        orderRepository.findByOrderedAtBetween(startAt, endAt)

    fun getOrder(orderId: Long) = orderRepository.findByIdOrNull(orderId) orThrowNotFound "해당하는 주문이 존재하지 않습니다."

    fun findAll(pageable: Pageable): Page<OrderModel> {
        return orderRepository.findAll(pageable)
    }
}
