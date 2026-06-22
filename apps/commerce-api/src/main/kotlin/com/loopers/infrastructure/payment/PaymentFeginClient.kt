package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentCommand
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import com.loopers.domain.payment.CardType as DomainCardType
import com.loopers.domain.payment.PaymentStatus as DomainPaymentStatus

@FeignClient(name = "pg", url = "\${pg.url}")
interface PaymentFeignClient {
    @PostMapping("/api/v1/payments")
    fun pay(
        @RequestHeader("X-USER-ID") userId: String,
        @RequestBody request: PaymentFeignRequest,
    ): PgResponse<TransactionResponse>
}

enum class CardType {
    SAMSUNG,
    KB,
    HYUNDAI,
    ;

    companion object {
        fun from(cardType: DomainCardType): CardType = when (cardType) {
            DomainCardType.SAMSUNG -> SAMSUNG
            DomainCardType.KB -> KB
            DomainCardType.HYUNDAI -> HYUNDAI
        }
    }
}

data class PaymentFeignRequest(
    val orderId: String,
    val cardType: CardType,
    val cardNo: String,
    val amount: Long,
    val callbackUrl: String,
) {
    companion object {
        fun from(command: PaymentCommand, callbackUrl: String): PaymentFeignRequest =
            PaymentFeignRequest(
                orderId = command.orderId.toString(),
                cardType = CardType.from(command.cardType),
                cardNo = command.cardNumber,
                amount = command.amount,
                callbackUrl = callbackUrl,
            )
    }
}

enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    ;

    fun toDomain(): DomainPaymentStatus = when (this) {
        PENDING -> DomainPaymentStatus.PENDING
        SUCCESS -> DomainPaymentStatus.SUCCESS
        FAILED -> DomainPaymentStatus.FAILED
    }
}

data class PgResponse<T>(
    val meta: Meta,
    val data: T?,
) {
    data class Meta(
        val result: String,
        val errorCode: String?,
        val message: String?,
    )
}

data class TransactionResponse(
    val transactionKey: String,
    val status: PaymentStatus,
    val reason: String?,
)
