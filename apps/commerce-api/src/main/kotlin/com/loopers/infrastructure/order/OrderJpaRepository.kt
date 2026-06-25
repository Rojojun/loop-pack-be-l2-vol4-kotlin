package com.loopers.infrastructure.order

import com.loopers.domain.order.OrderModel
import org.springframework.data.jpa.repository.JpaRepository
import java.time.ZonedDateTime

interface OrderJpaRepository : JpaRepository<OrderModel, Long> {
    fun findByOrderedAtBetween(startAt: ZonedDateTime, endAt: ZonedDateTime): List<OrderModel>
}
