package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentPort
import com.loopers.domain.payment.PaymentResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PaymentFeignAdapter(
    private val paymentFeignClient: PaymentFeignClient,
    @Value("\${pg.callback-url}")
    private val callbackUrl: String,
) : PaymentPort {
    override fun pay(command: PaymentCommand): PaymentResult {
        val request = PaymentFeignRequest.from(command, callbackUrl = callbackUrl)
        val response = paymentFeignClient.pay(
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
}
