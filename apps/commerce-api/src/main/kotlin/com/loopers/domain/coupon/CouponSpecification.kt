package com.loopers.domain.coupon

import com.loopers.support.error.ErrorType
import com.loopers.support.specification.Spec
import java.time.ZonedDateTime

class CouponOwnedBy(private val userId: Long): Spec<UserCouponModel>(
    errorMessage = "유저가 가진 쿠폰이 아닙니다.",
    errorType = ErrorType.FORBIDDEN,
) {
    override fun isSatisfiedBy(candidate: UserCouponModel): Boolean = candidate.userId == userId
}

class CouponNotExpired(private val now: ZonedDateTime): Spec<UserCouponModel>(
    errorMessage = "만료된 쿠폰은 사용할 수 없습니다.",
) {
    override fun isSatisfiedBy(candidate: UserCouponModel): Boolean = !candidate.coupon.isExpired(now)
}

class MinOrderAmountSatisfied(private val orderAmount: Double): Spec<UserCouponModel>(
    errorMessage = "쿠폰을 사용하려는 최소 금액에 적합하지 않습니다."
) {
    override fun isSatisfiedBy(candidate: UserCouponModel): Boolean = orderAmount >= candidate.coupon.minOrderAmount
}

object CouponNotUsed: Spec<UserCouponModel>(
    errorMessage = "사용할 수 있는 쿠폰이 아닙니다.",
    errorType = ErrorType.CONFLICT,
) {
    override fun isSatisfiedBy(candidate: UserCouponModel): Boolean = candidate.status == CouponStatus.AVAILABLE
}
