package com.loopers.domain.order

interface OrderRepository {
    fun save(orderModel: OrderModel): OrderModel
}
