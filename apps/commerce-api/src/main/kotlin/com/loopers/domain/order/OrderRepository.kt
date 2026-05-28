package com.loopers.domain.order

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

interface OrderRepository {
    fun save(orderModel: OrderModel): OrderModel

    fun findByIdOrNull(orderId: Long): OrderModel?

    fun findByOrderedAtBetween(startAt: ZonedDateTime, endAt: ZonedDateTime): List<OrderModel>

    fun findAll(pageable: Pageable): Page<OrderModel>
}
