package com.loopers.domain.payment

interface PaymentPort {
    fun pay(command: PaymentCommand): PaymentResult
}
