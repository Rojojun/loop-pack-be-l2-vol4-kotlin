package com.loopers.infrastructure.payment

import com.loopers.application.payment.PaymentFacade
import com.loopers.domain.payment.PaymentPort
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.PaymentStatus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.ZonedDateTime

@Component
class PaymentReconciler(
    private val paymentService: PaymentService,
    private val paymentFacade: PaymentFacade,
    @Value("\${payment.reconcile.stale-threshold}")
    private val staleThreshold: Duration,
    private val paymentPort: PaymentPort,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelayString = "\${payment.reconcile.interval}")
    fun reconcile() {
        val threshold = ZonedDateTime.now().minus(staleThreshold)
        paymentService.getAllByPendingAndCreatedAtBefore(threshold).forEach { payment ->
            runCatching {
                val transactionKey = payment.transactionKey
                if (transactionKey == null) {
                    log.warn("[reconcile] transactionKey 없는 요청 orderId = {} (PG 미접수)", payment.orderId)
                } else {
                    val result = paymentPort.getTransaction(payment.userId, transactionKey)
                    when (result.status) {
                        PaymentStatus.SUCCESS, PaymentStatus.FAILED -> paymentFacade.confirm(transactionKey, result.status, result.reason)
                        PaymentStatus.PENDING -> TODO("미정...")
                    }
                }
            }.onFailure { log.warn("[reconcile] 거래 처리 실패 orderId = {}, {}", payment.orderId, it.message) }
        }
    }
}
