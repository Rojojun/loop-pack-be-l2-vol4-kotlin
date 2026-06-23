package com.loopers.interfaces.api.payment

import com.loopers.domain.payment.PaymentModel
import com.loopers.domain.payment.PaymentStatus
import com.loopers.domain.payment.CardType as DomainCardType

class PaymentV1Dto {
    enum class CardType {
        SAMSUNG,
        KB,
        HYUNDAI,
        ;

        fun toDomain(): DomainCardType = when (this) {
            SAMSUNG -> DomainCardType.SAMSUNG
            KB -> DomainCardType.KB
            HYUNDAI -> DomainCardType.HYUNDAI
        }
    }

    data class PaymentRequest(
        val orderId: Long,
        val cardType: CardType,
        val cardNo: String,
    )

    data class PaymentResponse(
        val orderId: Long,
        val transactionKey: String?,
        val status: String,
    ) {
        companion object {
            fun from(payment: PaymentModel) = PaymentResponse(
                orderId = payment.orderId,
                transactionKey = payment.transactionKey,
                status = payment.status.name
            )
        }
    }

    data class PaymentCallbackRequest(
        val transactionKey: String,
        val orderId: String,
        val status: PaymentStatus,
        val reason: String?,
    )
}
