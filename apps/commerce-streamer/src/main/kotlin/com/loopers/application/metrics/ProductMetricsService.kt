package com.loopers.application.metrics

import com.loopers.infrastructure.metrics.EventHandledRepository
import com.loopers.infrastructure.metrics.ProductMetricRepository
import com.loopers.interfaces.consumer.message.LikeChangedMessage
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Component
class ProductMetricsService(
    private val productMetricRepository: ProductMetricRepository,
    private val eventHandledRepository: EventHandledRepository,
) {
    @Transactional
    fun applyLikeChanged(message: LikeChangedMessage) {
        if (eventHandledRepository.insertIfAbsent(message.eventId, ZonedDateTime.now()) == 0) return

        val delta = when (message.type) {
            LikeChangedMessage.LikedType.LIKED -> 1
            LikeChangedMessage.LikedType.UNLIKED -> -1
        }
        val version = message.occurredAt.toInstant().toEpochMilli()
        productMetricRepository.upsertLikeCount(message.productId, delta, version)
    }
}
