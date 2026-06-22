package com.loopers.interfaces.api.order

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payments")
class PaymentV1Controller(
    private val paymentFacade: PaymentFacade
) : PaymentV1ApiSpec {
    override fun pay(
        loginId: String,
        loginPassword: String,
        request: PaymentV1Dto.PaymentRequest,
    ) {
        TODO("Not yet implemented")
    }
}
