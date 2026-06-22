package com.loopers.domain.payment

data class PaymentResult(
    val orderId: Long,
    val status: PaymentStatus,
)

enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED
}
