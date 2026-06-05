package com.loopers.domain.stock

import com.loopers.domain.BaseEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "stocks")
class StockModel private constructor(
    productId: Long,
    quantity: Int,
) : BaseEntity() {

    @Column(name = "product_id", nullable = false, unique = true)
    var productId: Long = productId
        protected set

    @Column(name = "quantity", nullable = false)
    var quantity: Int = quantity
        protected set

    init {
        require(quantity >= 0) {
            throw CoreException(ErrorType.BAD_REQUEST, "재고 수량은 0 이상이어야 합니다.")
        }
    }

    fun reduce(amount: Int) {
        require(amount > 0) {
            throw CoreException(ErrorType.BAD_REQUEST, "차감 수량은 1 이상이어야 합니다.")
        }
        if (quantity < amount) {
            throw CoreException(ErrorType.BAD_REQUEST, "재고가 부족합니다.")
        }
        quantity -= amount
    }

    fun restore(amount: Int) {
        require(amount > 0) {
            throw CoreException(ErrorType.BAD_REQUEST, "복구 수량은 1 이상이어야 합니다.")
        }
        quantity += amount
    }

    fun isAvailable(required: Int): Boolean = quantity >= required

    fun isSoldOut(): Boolean = quantity == 0

    companion object {
        fun of(productId: Long, quantity: Int): StockModel = StockModel(productId, quantity)
    }
}
