package com.loopers.application.coupon

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.config.kafka.KafkaTopics
import com.loopers.domain.coupon.CouponIssueRequestModel
import com.loopers.domain.coupon.CouponIssueRequestRepository
import com.loopers.domain.coupon.CouponService
import com.loopers.domain.coupon.UserCouponService
import com.loopers.domain.outbox.OutboxModel
import com.loopers.domain.outbox.OutboxRepository
import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.UUID

@Component
class CouponFacade(
    private val couponService: CouponService,
    private val userCouponService: UserCouponService,
    private val userService: UserService,
    private val couponIssueRequestRepository: CouponIssueRequestRepository,
    private val outboxRepository: OutboxRepository,
    private val objectMapper: ObjectMapper,
) {
    fun issueCoupon(loginId: String, couponId: Long): MyCouponInfo {
        val user = userService.getByLoginId(loginId)
        val coupon = couponService.getCoupon(couponId)
        val userCoupon = userCouponService.issueCoupon(user.id, coupon)
        val now = ZonedDateTime.now()

        return MyCouponInfo.from(userCoupon, now)
    }

    fun getCoupons(loginId: String): List<MyCouponInfo> {
        val user = userService.getByLoginId(loginId)
        val now = ZonedDateTime.now()
        return userCouponService.getByUserId(user.id)
            .map { MyCouponInfo.from(it, now) }
    }

    @Transactional
    fun requestIssue(loginId: String, couponId: Long): String {
        val user = userService.getByLoginId(loginId)
        val coupon = couponService.getCoupon(couponId)

        val requestId = UUID.randomUUID().toString()

        couponIssueRequestRepository.save(CouponIssueRequestModel.of(requestId = requestId, couponId = coupon.id, userId = user.id))
        outboxRepository.save(OutboxModel.of(
            aggregateId = coupon.id.toString(),
            eventType = "coupon_issued",
            topic = KafkaTopics.COUPON_ISSUE_REQUESTS,
            payload = objectMapper.writeValueAsString(
                CouponIssueRequestPayload(requestId = requestId, couponId = coupon.id, userId = user.id)
            )
        ))

        return requestId
    }
}

data class CouponIssueRequestPayload(
    val requestId: String,
    val couponId: Long,
    val userId: Long,
)
