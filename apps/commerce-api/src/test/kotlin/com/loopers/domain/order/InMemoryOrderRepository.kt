package com.loopers.domain.order

import com.loopers.domain.BaseEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

class InMemoryOrderRepository : OrderRepository {
    private val data: MutableMap<Long, OrderModel> = HashMap()
    private var sequence = 1L

    override fun save(orderModel: OrderModel): OrderModel {
        val id = sequence++
        try {
            val idField = BaseEntity::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(orderModel, id)
            idField.isAccessible = false
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        data[id] = orderModel
        return orderModel
    }

    override fun findByIdOrNull(orderId: Long): OrderModel? = data[orderId]

    override fun findByOrderedAtBetween(
        startAt: ZonedDateTime,
        endAt: ZonedDateTime,
    ): List<OrderModel> = data.values.filter { it.orderedAt in startAt..endAt }

    override fun findAll(pageable: Pageable): Page<OrderModel> {
        TODO("Not yet implemented")
    }
}
