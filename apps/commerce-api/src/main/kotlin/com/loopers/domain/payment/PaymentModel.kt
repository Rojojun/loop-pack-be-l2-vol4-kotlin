package com.loopers.domain.payment

import com.loopers.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "payments")
class PaymentModel private constructor (
    orderId: Long,
    userId: String,
    cardType: CardType,
    amount: Long,
    transactionKey: String?,
) : BaseEntity() {
    @Column(name = "order_id", nullable = false, unique = true)
    var orderId: Long = orderId
        protected set

    @Column(name = "user_id", nullable = false)
    var userId: String = userId
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    var cardType: CardType = cardType
        protected set

    @Column(name = "amount", nullable = false)
    var amount: Long = amount
        protected set

    @Column(name = "transaction_key", nullable = true)
    var transactionKey: String? = transactionKey
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PaymentStatus = PaymentStatus.PENDING
        protected set

    @Column(name = "reason", nullable = true)
    var reason: String? = null
        protected set

    fun markRequested(transactionKey: String) {
        if (status != PaymentStatus.PENDING) return
        this.transactionKey = transactionKey
    }

    fun confirmSuccess() {
        if (status != PaymentStatus.PENDING) return
        this.status = PaymentStatus.SUCCESS
    }

    fun confirmFailure(reason: String) {
        if (status != PaymentStatus.PENDING) return
        this.status = PaymentStatus.FAILED
        this.reason = reason
    }

    companion object {
        fun of(orderId: Long, userId: String, cardType: CardType, amount: Long, transactionKey: String?) =
            PaymentModel(
                orderId = orderId,
                userId = userId,
                cardType = cardType,
                amount = amount,
                transactionKey = transactionKey
            )
    }
}
