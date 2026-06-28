package com.loopers.domain.payment

data class PaymentCommand(
    val orderId: Long,
    val userId: String,
    val cardType: CardType,
    val cardNumber: String,
    val amount: Long,
)

enum class CardType {
    SAMSUNG,
    KB,
    HYUNDAI,
}
