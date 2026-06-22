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
        val request = PaymentFeignRequest.from(command, callBackUrl = callbackUrl)
        val response = paymentFeignClient.pay(
            userId = command.userId,
            request = request,
        )
        return response.toResult();
    }
}
