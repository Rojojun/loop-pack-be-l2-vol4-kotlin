package com.loopers.domain.payment

interface PaymentPort {
    fun requestPayment(command: PaymentCommand): PaymentResult

    fun getTransaction(userId: String, transactionKey: String): PaymentResult
}
