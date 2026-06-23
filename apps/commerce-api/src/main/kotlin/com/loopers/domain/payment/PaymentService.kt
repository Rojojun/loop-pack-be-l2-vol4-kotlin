package com.loopers.domain.payment

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentService(
    private val paymentRepository: PaymentRepository
) {
    @Transactional
    fun getOrCreatePending(orderId: Long, userId: String, cardType: CardType, amount: Long): PaymentModel {
        paymentRepository.findByOrderId(orderId)?.let { return it }
        return try {
            paymentRepository.save(
                PaymentModel.of(orderId, userId, cardType, amount, transactionKey = null)
            )
        } catch (e: DataIntegrityViolationException) {
            paymentRepository.findByOrderId(orderId) ?: throw e
        }
    }

    @Transactional
    fun addTransactionKey(orderId: Long, transactionKey: String) {
        val payment = paymentRepository.findByOrderId(orderId) ?: return
        payment.markRequested(transactionKey)
        paymentRepository.save(payment)
    }

    @Transactional
    fun confirm(transactionKey: String, success: Boolean, reason: String?): Boolean? {
        val payment = paymentRepository.findByTransactionKey(transactionKey) ?: return null
        if (payment.status != PaymentStatus.PENDING) return false
        if (success) payment.confirmSuccess() else payment.confirmFailure(reason ?: "결제 실패")
        paymentRepository.save(payment)
        return true
    }

    fun findByTransactionKey(transactionKey: String): PaymentModel? =
        paymentRepository.findByTransactionKey(transactionKey)

    fun findByOrderId(orderId: Long): PaymentModel? = paymentRepository.findByOrderId(orderId)
}
