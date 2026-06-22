package com.loopers.domain.payment

data class PaymentResult(
    val orderId: Long,
    val transactionKey: String,
    val status: PaymentStatus,
    val reason: String?,
)

enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
}
