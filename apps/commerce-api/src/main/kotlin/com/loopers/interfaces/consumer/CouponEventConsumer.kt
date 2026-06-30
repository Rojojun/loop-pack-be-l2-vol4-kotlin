package com.loopers.interfaces.consumer

import com.loopers.application.coupon.CouponFacade
import com.loopers.application.coupon.CouponIssueRequestPayload
import com.loopers.config.kafka.KafkaConfig
import com.loopers.config.kafka.KafkaTopics.COUPON_ISSUE_REQUESTS
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class CouponEventConsumer(
    private val couponFacade: CouponFacade,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = [COUPON_ISSUE_REQUESTS],
        groupId = "coupon-issue",
        containerFactory = KafkaConfig.BATCH_LISTENER,
    )
    fun consume(message: List<CouponIssueRequestPayload>, ack: Acknowledgment) {
        message.forEach { payload ->
            runCatching { couponFacade.issue(payload) }
                .onFailure { log.error("Error in consuming coupon requestId=${payload.requestId}", it) }
        }
        ack.acknowledge()
    }
}
