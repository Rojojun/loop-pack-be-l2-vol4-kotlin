package com.loopers.domain.order

interface OrderRepository {
    fun save(orderModel: OrderModel): OrderModel

    fun findByIdOrNull(orderId: Long): OrderModel?
}
