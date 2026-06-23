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

    fun findByOrderId(orderId: Long): PaymentModel? = paymentRepository.findByOrderId(orderId)
}
