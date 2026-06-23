package com.loopers.support.error

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentPort
import com.loopers.domain.payment.PaymentResult
import com.loopers.domain.payment.PaymentStatus

class FakePaymentPort : PaymentPort {
    val pay : (PaymentCommand) -> PaymentResult = { command ->
        PaymentResult(command.orderId, "TR-${command.orderId}", PaymentStatus.PENDING, null)
    }

    val transactions: MutableMap<String, PaymentResult> = mutableMapOf()

    override fun requestPayment(command: PaymentCommand): PaymentResult = pay(command)

    override fun getTransaction(userId: String, transactionKey: String): PaymentResult = transactions[transactionKey] ?: error("Transaction $transactionKey not found")
}
