package com.loopers.infrastructure.order

import com.loopers.domain.order.OrderModel
import com.loopers.domain.order.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Component
class OrderRepositoryImpl(
    private val orderJpaRepository: OrderJpaRepository
) : OrderRepository {
    override fun save(orderModel: OrderModel): OrderModel =
        orderJpaRepository.save(orderModel)

    override fun findByIdOrNull(orderId: Long): OrderModel? =
        orderJpaRepository.findByIdOrNull(orderId)

    override fun findByOrderedAtBetween(startAt: ZonedDateTime, endAt: ZonedDateTime): List<OrderModel> =
        orderJpaRepository.findByOrderedAtBetween(startAt, endAt)

    override fun findAll(pageable: Pageable): Page<OrderModel> {
        return orderJpaRepository.findAll(pageable)
    }
}
