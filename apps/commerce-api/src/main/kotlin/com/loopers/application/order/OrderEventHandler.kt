package com.loopers.application.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.config.kafka.KafkaTopics
import com.loopers.domain.order.OrderEvent
import com.loopers.domain.outbox.OutboxModel
import com.loopers.domain.outbox.OutboxRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OrderEventHandler(
    private val outboxRepository: OutboxRepository,
    private val objectMapper: ObjectMapper,
) {
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun toOutbox(event: OrderEvent.Confirmed) {
        outboxRepository.save(
            OutboxModel.of(
                aggregateId = event.orderId.toString(),
                eventType = "OrderConfirmed",
                topic = KafkaTopics.ORDER_EVENTS,
                payload = objectMapper.writeValueAsString(event),
            ),
        )
    }
}
