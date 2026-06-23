package com.loopers.application.payment

import com.loopers.domain.payment.CardType
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentModel
import com.loopers.domain.payment.PaymentPort
import com.loopers.domain.payment.PaymentService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PaymentFacade(
    private val paymentService: PaymentService,
    private val paymentPort: PaymentPort
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun requestPayment(userId: String, orderId: Long, cardType: CardType, cardNumber: String, amount: Long): PaymentModel {
        val payment = paymentService.getOrCreatePending(orderId, userId, cardType, amount)
        if (payment.transactionKey != null) return payment

        runCatching {
            paymentPort.requestPayment(PaymentCommand(orderId, userId, cardType, cardNumber, amount))
        }.onSuccess {
            result -> paymentService.addTransactionKey(orderId, result.transactionKey)
        }.onFailure {
            log.warn("PG 결제 요청이 실패하였습니다. 주문ID= {}, reason={}", orderId, it.message)
        }
        return paymentService.findByOrderId(orderId)!!
    }
}
