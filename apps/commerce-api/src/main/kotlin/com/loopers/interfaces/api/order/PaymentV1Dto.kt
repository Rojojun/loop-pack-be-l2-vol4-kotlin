package com.loopers.interfaces.api.order

class PaymentV1Dto {
    enum class CardType {
        SAMSUNG
    }

    data class PaymentRequest(
        val orderId: Long,
        val cardType: CardType,
        val cardNo: String,
    )

    data class PaymentResponse(
        val orderId: Long,
        val status: String,
    ) {
        companion object {
            fun from(info: PaymentInfo): PaymentResponse =
                PaymentResponse(info.orderId, info.status.name)
        }
    }
}
