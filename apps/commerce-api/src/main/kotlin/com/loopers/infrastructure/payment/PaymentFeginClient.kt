package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentResult
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import com.loopers.domain.payment.CardType as DomainCardType
import com.loopers.domain.payment.PaymentStatus as DomainPaymentStaus

@FeignClient(name = "pg", url="\${pg.url}")
interface PaymentFeignClient {
    @PostMapping
    fun pay(
        @RequestHeader("X-USER-ID") userId: String,
        @RequestBody request: PaymentFeignRequest
    ): PaymentFeignResponse
}

enum class CardType {
    SAMSUNG;

    companion object {
        fun from(cardType: DomainCardType): CardType = when (cardType) {
            DomainCardType.SAMSUNG -> SAMSUNG
        }
    }
}

data class PaymentFeignRequest(
    val orderId: Long,
    val cardType: CardType,
    val cardNo: String,
    val amount: Double,
    val callBackUrl: String,
) {
    companion object {
        fun from(command: PaymentCommand, callBackUrl: String): PaymentFeignRequest =
            PaymentFeignRequest(
                orderId = command.orderId,
                cardType = CardType.from(command.cardType),
                cardNo = command.cardNumber,
                amount = command.amount,
                callBackUrl = callBackUrl,
            )
    }
}

enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED
    ;

    fun toDomain(): DomainPaymentStaus = when (this) {
        PENDING -> DomainPaymentStaus.PENDING
        SUCCESS -> DomainPaymentStaus.SUCCESS
        FAILED -> DomainPaymentStaus.FAILED
    }
}

data class PaymentFeignResponse(
    val orderId: Long,
    val status: PaymentStatus,
) {
    fun toResult(): PaymentResult =
        PaymentResult(
            orderId = orderId,
            status = status.toDomain(),
        )
}
