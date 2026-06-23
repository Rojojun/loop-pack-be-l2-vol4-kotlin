package com.loopers.domain.payment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PaymentTest {

    private fun pending() = Payment(
        orderId = 1L,
        userId = "user1",
        cardType = CardType.SAMSUNG,
        amount = 10_000L,
    )

    @Test
    @DisplayName("생성 직후 상태는 PENDING, transactionKey는 null이다")
    fun createdAsPending() {
        val payment = pending()
        assertThat(payment.status).isEqualTo(PaymentStatus.PENDING)
        assertThat(payment.transactionKey).isNull()
    }

    @Test
    @DisplayName("markRequested 는 PENDING 에서 transactionKey 를 부여한다")
    fun markRequested() {
        val payment = pending()
        payment.markRequested("TR-1")
        assertThat(payment.transactionKey).isEqualTo("TR-1")
        assertThat(payment.status).isEqualTo(PaymentStatus.PENDING)
    }

    @Test
    @DisplayName("confirmSuccess 는 PENDING 에서만 SUCCESS 로 전이한다")
    fun confirmSuccess() {
        val payment = pending()
        payment.confirmSuccess()
        assertThat(payment.status).isEqualTo(PaymentStatus.SUCCESS)
    }

    @Test
    @DisplayName("이미 SUCCESS 면 confirmFailure 는 no-op 이다 (멱등)")
    fun confirmIsIdempotent() {
        val payment = pending()
        payment.confirmSuccess()
        payment.confirmFailure("한도초과") // no-op
        assertThat(payment.status).isEqualTo(PaymentStatus.SUCCESS)
        assertThat(payment.reason).isNull()
    }

    @Test
    @DisplayName("confirmFailure 는 PENDING 에서 FAILED + reason 으로 전이한다")
    fun confirmFailure() {
        val payment = pending()
        payment.confirmFailure("한도초과입니다.")
        assertThat(payment.status).isEqualTo(PaymentStatus.FAILED)
        assertThat(payment.reason).isEqualTo("한도초과입니다.")
    }

    @Test
    @DisplayName("이미 확정된 결제는 markRequested 가 no-op 이다")
    fun markRequestedIsNoOpWhenConfirmed() {
        val payment = pending()
        payment.confirmSuccess()
        payment.markRequested("TR-late")
        assertThat(payment.transactionKey).isNull()
    }
}
