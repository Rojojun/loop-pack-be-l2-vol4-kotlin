package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentPort
import com.loopers.domain.payment.PaymentResult
import feign.FeignException
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PaymentFeignAdapter(
    private val paymentFeignClient: PaymentFeignClient,
    @Value("\${pg.callback-url}")
    private val callbackUrl: String,
) : PaymentPort {
    @Retry(name = "pg")
    @CircuitBreaker(name = "pg")
    override fun requestPayment(command: PaymentCommand): PaymentResult {
        val request = PaymentFeignRequest.from(command, callbackUrl = callbackUrl)
        val response = paymentFeignClient.requestPayment(
            userId = command.userId,
            request = request,
        )
        val transaction = response.data ?: error("PG 응답에 거래 정보(data)가 없습니다. meta=${response.meta}")
        return PaymentResult(
            orderId = command.orderId,
            transactionKey = transaction.transactionKey,
            status = transaction.status.toDomain(),
            reason = transaction.reason,
        )
    }

    @Retry(name = "pg")
    @CircuitBreaker(name = "pg")
    override fun getTransaction(userId: String, transactionKey: String): PaymentResult {
        val response = paymentFeignClient.getTransaction(userId, transactionKey)
        val transaction = response.data
            ?: error("PG 조회 응답에 거래 정보(data)가 존재하지 않습니다. meta=${response.meta}")
        return PaymentResult(
            orderId = 0L,
            transactionKey = transaction.transactionKey,
            status = transaction.status.toDomain(),
            reason = transaction.reason,
        )
    }

    @CircuitBreaker(name = "pg")
    override fun getAllByOrderId(userId: String, orderId: Long): List<PaymentResult> {
        val response = try {
            paymentFeignClient.findByOrderId(
                userId = userId,
                orderId = orderId.toString().padStart(6, '0'),
            )
        } catch (e: FeignException.NotFound) {
            return emptyList()
        }
        val order = response.data ?: return emptyList()

        return order.transactions.map { transaction ->
            PaymentResult(
                orderId = orderId,
                transactionKey = transaction.transactionKey,
                status = transaction.status.toDomain(),
                reason = transaction.reason,
            )
        }
    }
}
