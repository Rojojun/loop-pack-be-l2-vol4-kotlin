package com.loopers.interfaces.api.payment

import com.loopers.application.payment.PaymentFacade
import com.loopers.domain.payment.PaymentStatus
import com.loopers.interfaces.api.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payments")
class PaymentV1Controller(
    private val paymentFacade: PaymentFacade
) : PaymentV1ApiSpec {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping
    override fun pay(
        @RequestHeader("X-Loopers-LoginId") loginId: String,
        @RequestHeader("X-Loopers-LoginPw") loginPassword: String,
        @RequestBody request: PaymentV1Dto.PaymentRequest,
    ): ApiResponse<PaymentV1Dto.PaymentResponse> {
        val payment = paymentFacade.requestPayment(
            userId = loginId,
            orderId = request.orderId,
            cardType = request.cardType.toDomain(),
            cardNumber = request.cardNo,
        )
        return ApiResponse.success(PaymentV1Dto.PaymentResponse.from(payment))
    }

    @PostMapping("/callback")
    fun callback(@RequestBody request: PaymentV1Dto.PaymentCallbackRequest) {
        if (request.status == PaymentStatus.PENDING) {
            log.info("PENDING 콜백 무시 transactionKey={}", request.transactionKey)
            return
        }
        paymentFacade.confirm(request.transactionKey, request.status, request.reason)
    }
}
