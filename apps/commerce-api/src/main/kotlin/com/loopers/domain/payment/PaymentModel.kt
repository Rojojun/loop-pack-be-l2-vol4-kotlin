package com.loopers.domain.payment

import com.loopers.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "payments")
class Payment(
    @Column(name = "order_id", nullable = false, unique = true)
    val orderId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    val cardType: CardType,

    @Column(name = "amount", nullable = false)
    val amount: Long,
) : BaseEntity() {

    @Column(name = "transaction_key", nullable = true)
    var transactionKey: String? = null
        private set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PaymentStatus = PaymentStatus.PENDING
        private set

    @Column(name = "reason", nullable = true)
    var reason: String? = null
        private set

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
}
