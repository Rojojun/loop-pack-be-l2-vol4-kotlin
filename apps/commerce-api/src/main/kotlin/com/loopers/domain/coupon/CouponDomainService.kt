package com.loopers.domain.coupon

import com.loopers.support.function.ensure
import java.time.ZonedDateTime

fun applyCoupon(userCoupon: UserCouponModel, userId: Long, orderAmount: Double, now: ZonedDateTime): Double =
    userCoupon
        .ensure(CouponOwnedBy(userId))
        .ensure(CouponNotExpired(now))
        .ensure(MinOrderAmountSatisfied(orderAmount))
        .ensure(CouponNotUsed)
        .also { it.use() }
        .coupon.discount(orderAmount)
