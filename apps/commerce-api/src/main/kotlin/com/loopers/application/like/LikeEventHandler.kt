package com.loopers.application.like

import com.loopers.domain.like.LikeEvent
import com.loopers.domain.like.LikeService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class LikeEventHandler(
    private val likeService: LikeService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: LikeEvent.Changed) {
        runCatching { likeService.applyLikeCount(event) }
            .onFailure { log.warn("좋아요 집계 실패 productId={}, {}", event.productId, it.message) }
    }
}
