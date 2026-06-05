package com.loopers.application.coupon

import com.loopers.domain.coupon.CouponStatus
import com.loopers.domain.coupon.CouponType
import com.loopers.domain.coupon.UserCouponModel
import java.time.ZonedDateTime

data class MyCouponInfo private constructor(
    val userCouponId: Long,
    val name: String,
    val type: CouponType,
    val value: Double,
    val minOrderAmount: Double,
    val expiredAt: ZonedDateTime,
    val status: CouponStatus,
) {
    companion object {
        fun from(userCoupon: UserCouponModel, now: ZonedDateTime): MyCouponInfo =
            MyCouponInfo(
                userCouponId = userCoupon.id,
                name = userCoupon.coupon.name,
                type = userCoupon.coupon.type,
                value = userCoupon.coupon.value,
                minOrderAmount = userCoupon.coupon.minOrderAmount,
                expiredAt = userCoupon.coupon.expiredAt,
                status = userCoupon.statusAt(now),
            )
    }
}
