package com.loopers.infrastructure.payment

import com.loopers.domain.payment.CardType
import com.loopers.domain.payment.PaymentModel
import com.loopers.domain.payment.PaymentRepository
import com.loopers.domain.payment.PaymentStatus
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.ZonedDateTime

@SpringBootTest
class PaymentRepositoryImplIntegrationTest @Autowired constructor(
    private val paymentRepository: PaymentRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {
    @AfterEach
    fun tearDown() = databaseCleanUp.truncateAllTables()

    private fun payment(orderId: Long) = PaymentModel.of(
        orderId = orderId,
        userId = "user1",
        cardType = CardType.SAMSUNG,
        amount = 10_000L,
        transactionKey = null,
    )

    @Test
    @DisplayName("orderId 로 결제건을 조회한다")
    fun findByOrderId() {
        paymentRepository.save(payment(1L))
        assertThat(paymentRepository.findByOrderId(1L)).isNotNull
        assertThat(paymentRepository.findByOrderId(999L)).isNull()
    }

    @Test
    @DisplayName("transactionKey 로 결제건을 조회한다")
    fun findByTransactionKey() {
        val saved = payment(1L).apply { markRequested("TR-1") }
        paymentRepository.save(saved)
        assertThat(paymentRepository.findByTransactionKey("TR-1")).isNotNull
    }

    @Test
    @DisplayName("PENDING 이면서 createdAt 이 임계 이전인 건만 조회한다")
    fun findStalePending() {
        paymentRepository.save(payment(1L)) // PENDING, createdAt=now
        val future = ZonedDateTime.now().plusMinutes(10)
        val stale = paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.PENDING, future)
        assertThat(stale).hasSize(1)
    }
}
