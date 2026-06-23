package com.loopers.application.payment

import com.loopers.domain.order.OrderService
import com.loopers.domain.order.OrderStatus
import com.loopers.domain.payment.CardType
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentModel
import com.loopers.domain.payment.PaymentPort
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.PaymentStatus
import com.loopers.domain.stock.StockService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentFacade(
    private val paymentService: PaymentService,
    private val orderService: OrderService,
    private val stockService: StockService,
    private val paymentPort: PaymentPort
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun requestPayment(userId: String, orderId: Long, cardType: CardType, cardNumber: String): PaymentModel {
        val amount = orderService.getOrder(orderId).finalAmount.toLong()
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

    @Transactional
    fun confirm(transactionKey: String, status: PaymentStatus, reason: String?) {
        val isSuccessTransaction = paymentService.confirm(
            transactionKey = transactionKey,
            success = status == PaymentStatus.SUCCESS,
            reason = reason,
        )
        if (isSuccessTransaction != true) {
            log.info("주문을 확인해주세요. [멱등조건 위반] transactionKey= {}, status = {}", transactionKey, status)
            return
        }

        val orderId = paymentService.findByTransactionKey(transactionKey)!!.orderId
        if (status == PaymentStatus.SUCCESS) {
            orderService.getOrder(orderId).confirm()
        } else {
            val order = orderService.getOrder(orderId)
            if (order.status != OrderStatus.CANCELLED) {
                order.items.forEach { item ->
                    val stock = stockService.getStockById(item.productId)
                    stockService.restoreStock(stock, item.quantity)
                }
                order.markCancel()
            }
        }
    }
}
