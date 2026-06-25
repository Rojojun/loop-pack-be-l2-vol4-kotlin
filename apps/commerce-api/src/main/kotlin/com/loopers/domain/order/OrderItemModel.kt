package com.loopers.domain.order

import com.loopers.domain.BaseEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "order_item")
class OrderItemModel private constructor(
    productId: Long,
    productName: String,
    unitPrice: Double,
    quantity: Int,
): BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: OrderModel? = null
        protected set

    var productId: Long = productId
        protected set

    var productName: String = productName
        protected set

    var unitPrice: Double = unitPrice
        protected set

    var quantity: Int = quantity
        protected set

    init {
        require(quantity > 0) { throw CoreException(ErrorType.BAD_REQUEST, "수량은 1 이상이어야 합니다.") }
        require(unitPrice >= 0) { throw CoreException(ErrorType.BAD_REQUEST, "단가는 0 이상이어야 합니다.") }
    }

    fun totalPrice(): Double = unitPrice * quantity

    internal fun assignTo(orderModel: OrderModel) {
        this.order = orderModel
    }

    companion object {
        fun of(productId: Long, productName: String, unitPrice: Double, quantity: Int): OrderItemModel =
            OrderItemModel(productId, productName, unitPrice, quantity)
    }
}
