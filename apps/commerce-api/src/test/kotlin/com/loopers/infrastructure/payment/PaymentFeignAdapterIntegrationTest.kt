package com.loopers.infrastructure.payment

import com.loopers.domain.payment.CardType
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentPort
import com.loopers.domain.payment.PaymentResult
import com.loopers.domain.payment.PaymentStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@DisplayName("PaymentFeignAdapter 통합 - 실제 pg-simulator(8082) 호출")
class PaymentFeignAdapterIntegrationTest @Autowired constructor(
    private val paymentPort: PaymentPort,
) {
    @Test
    @DisplayName("결제 요청 시 PG의 ApiResponse 래핑을 풀어 transactionKey/PENDING 으로 번역한다")
    fun pay_returnsTransactionKeyAndPending() {
        // given
        val command = PaymentCommand(
            orderId = 1351039135L,
            userId = "tester",
            cardType = CardType.SAMSUNG,
            cardNumber = "1234-5678-9814-1451",
            amount = 5000L,
        )

        var result: PaymentResult? = null
        for (attempt in 1..10) {
            try {
                result = paymentPort.pay(command)
                println("시도 $attempt 성공 → $result")
                break
            } catch (e: Exception) {
                println("시도 $attempt 실패: ${e.javaClass.simpleName} - ${e.message}")
            }
        }

        // then
        assertThat(result).isNotNull
        assertThat(result!!.orderId).isEqualTo(1351039135L)
        assertThat(result.transactionKey).contains(":TR:")
        assertThat(result.status).isEqualTo(PaymentStatus.PENDING)
    }
}
