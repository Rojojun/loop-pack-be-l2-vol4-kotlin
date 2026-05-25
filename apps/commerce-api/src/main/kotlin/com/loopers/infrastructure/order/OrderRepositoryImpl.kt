package com.loopers.infrastructure.order

import com.loopers.domain.order.OrderModel
import com.loopers.domain.order.OrderRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class OrderRepositoryImpl(
    private val orderJpaRepository: OrderJpaRepository
) : OrderRepository {
    override fun save(orderModel: OrderModel): OrderModel =
        orderJpaRepository.save(orderModel)

    override fun findByIdOrNull(orderId: Long): OrderModel? =
        orderJpaRepository.findByIdOrNull(orderId)
}
