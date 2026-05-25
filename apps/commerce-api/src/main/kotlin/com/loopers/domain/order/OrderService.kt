package com.loopers.domain.order

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.support.function.orThrowNotFound
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

    fun deleteOrder(orderId: Long, userId: Long) =
        orderRepository.findByIdOrNull(orderId)
            .orThrowNotFound("해당하는 주문이 존재하지 않습니다.")
            .also { if(it.userId != userId) { throw CoreException(ErrorType.BAD_REQUEST, "해당하는 유저의 주문이 아닙니다.")} }
            .cancel()
}
