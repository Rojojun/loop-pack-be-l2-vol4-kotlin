package com.loopers.domain.order

import com.loopers.domain.BaseEntity
import com.loopers.support.function.ensure
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Entity
@Table(name = "orders")
class OrderModel private constructor (
    userId: Long,
    status: OrderStatus,
    orderedAt: ZonedDateTime,
    couponId: Long?,
    discountAmount: Double,
    finalAmount: Double,
): BaseEntity() {
    var userId: Long = userId
        protected set

    @Enumerated(EnumType.STRING)
    var status = status
        protected set

    var orderedAt = orderedAt
        protected set

    var couponId: Long? = couponId
        protected set

    var discountAmount: Double = discountAmount
        protected set

    var finalAmount: Double = finalAmount
        protected set

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val orderItem: MutableList<OrderItemModel> = mutableListOf()

    fun cancel(userId: Long) {
        this.ensure(OrderOwnedBy(userId))
        this.ensure(OrderIsCancellable)
        this.status = OrderStatus.CANCELLED
        this.delete()
        this.items.forEach { it.delete() }
    }

    val items: List<OrderItemModel>
        get() = orderItem.toList()

    fun totalPrice(): Double = orderItem.sumOf { it.totalPrice() }

    fun validateOwnedBy(userId: Long) = this.ensure(OrderOwnedBy(userId))

    fun confirm() {
        if (status != OrderStatus.PENDING) return
        this.status = OrderStatus.CONFIRMED
    }

    fun markCancel() {
        if (status == OrderStatus.CANCELLED) return
        this.status = OrderStatus.CANCELLED
    }

    private fun addItem(item: OrderItemModel) =
        item.also { orderItem.add(it) }
            .also { it.assignTo(this) }

    companion object {
        fun of(userId: Long, items: List<OrderItemModel>, couponId: Long? = null, discountAmount: Double = 0.0): OrderModel =
            items.ensure(OrderItemsNotEmpty)
                .run { OrderModel(
                    userId = userId,
                    status = OrderStatus.PENDING,
                    orderedAt = ZonedDateTime.now(),
                    couponId = couponId,
                    discountAmount = discountAmount,
                    finalAmount = (items.sumOf { it.totalPrice() } - discountAmount).coerceAtLeast(0.0) ,
                ) }
                .also { order -> items.forEach { order.addItem(it) } }
    }
}
