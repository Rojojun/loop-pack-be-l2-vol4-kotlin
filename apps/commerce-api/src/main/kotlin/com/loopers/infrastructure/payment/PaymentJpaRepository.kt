package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentModel
import com.loopers.domain.payment.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.ZonedDateTime

interface PaymentJpaRepository : JpaRepository<PaymentModel, Long> {
    fun findByOrderId(orderId: Long): PaymentModel?

    fun findByTransactionKey(transactionKey: String): PaymentModel?

    fun findByStatusAndCreatedAtBefore(status: PaymentStatus, threshold: ZonedDateTime): List<PaymentModel>
}
