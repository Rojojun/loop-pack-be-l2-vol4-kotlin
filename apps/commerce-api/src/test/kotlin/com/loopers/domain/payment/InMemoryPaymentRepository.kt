package com.loopers.domain.payment

import com.loopers.domain.BaseEntity
import org.springframework.dao.DataIntegrityViolationException
import java.time.ZonedDateTime

class InMemoryPaymentRepository : PaymentRepository {
    private val data: MutableMap<Long, PaymentModel> = HashMap()
    private var sequence = 1L

    override fun save(payment: PaymentModel): PaymentModel {
        val existing = data.values.firstOrNull { it.orderId == payment.orderId }
        if (existing != null && existing.id != payment.id) {
            throw DataIntegrityViolationException("duplicate orderId=${payment.orderId}")
        }
        val id = if (payment.id == 0L) sequence++ else payment.id
        val idField = BaseEntity::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(payment, id)
        idField.isAccessible = false
        data[id] = payment
        return payment
    }

    override fun findByOrderId(orderId: Long): PaymentModel? =
        data.values.firstOrNull { it.orderId == orderId }

    override fun findByTransactionKey(transactionKey: String): PaymentModel? =
        data.values.firstOrNull { it.transactionKey == transactionKey }

    override fun findByStatusAndCreatedAtBefore(status: PaymentStatus, threshold: ZonedDateTime): List<PaymentModel> =
        data.values.filter { it.status == status && it.createdAt.isBefore(threshold) }

    private fun setId(entity: PaymentModel, id: Long) {
        val idField = BaseEntity::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(entity, id)
        idField.isAccessible = false
    }
}
