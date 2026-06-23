package com.loopers.application.payment

import com.loopers.domain.payment.CardType
import com.loopers.domain.payment.InMemoryPaymentRepository
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.PaymentStatus
import com.loopers.support.error.FakePaymentPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PaymentFacadeTest {
    private val inMemoryPaymentRepository = InMemoryPaymentRepository()
    private val fakePaymentPort = FakePaymentPort()

    private val paymentService = PaymentService(inMemoryPaymentRepository)
    private val paymentFacade = PaymentFacade(paymentService, fakePaymentPort)

    @Nested
    @DisplayName("결제를 요청할 때")
    inner class RequestPayment {
        @DisplayName("정상 요청이면 PENDING 상태로 접수되고 transactionKey 가 부여된다.")
        @Test
        fun returnsPendingWithTransactionKey() {
            // when
            val payment = paymentFacade.requestPayment(
                userId = "user1",
                orderId = 1L,
                cardType = CardType.SAMSUNG,
                cardNumber = "1234-1234-1234-1234",
                amount = 10_000L,
            )

            // then
            assertThat(payment.status).isEqualTo(PaymentStatus.PENDING)
            assertThat(payment.transactionKey).isEqualTo("TR-1")
        }

        @DisplayName("PG 호출이 실패해도 예외 없이 PENDING(transactionKey=null) 으로 유지된다.")
        @Test
        fun keepsPendingWhenPgFails() {
            // given
            fakePaymentPort.pay = { throw RuntimeException("PG 500") }

            // when
            val payment = paymentFacade.requestPayment(
                userId = "user1",
                orderId = 2L,
                cardType = CardType.SAMSUNG,
                cardNumber = "1234-1234-1234-1234",
                amount = 10_000L,
            )

            // then
            assertThat(payment.status).isEqualTo(PaymentStatus.PENDING)
            assertThat(payment.transactionKey).isNull()
        }

        @DisplayName("이미 transactionKey 가 부여된 주문은 PG 를 재호출하지 않는다.")
        @Test
        fun doesNotRecallPgWhenAlreadyRequested() {
            // given
            paymentFacade.requestPayment("user1", 3L, CardType.SAMSUNG, "1234-1234-1234-1234", 10_000L)

            // when
            val again = paymentFacade.requestPayment("user1", 3L, CardType.SAMSUNG, "1234-1234-1234-1234", 10_000L)

            // then
            assertThat(again.transactionKey).isEqualTo("TR-3")
        }
    }
}
