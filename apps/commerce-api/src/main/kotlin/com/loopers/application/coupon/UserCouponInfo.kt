package com.loopers.application.coupon

import com.loopers.domain.coupon.UserCouponModel
import java.time.ZonedDateTime

data class UserCouponInfo private constructor(
    val userId: Long,
    val issuedAt: ZonedDateTime,
    val usedAt: ZonedDateTime?
) {
    companion object {
        fun from(userCoupon: UserCouponModel): UserCouponInfo =
            UserCouponInfo(
                userId = userCoupon.id,
                issuedAt = userCoupon.issuedAt,
                usedAt = userCoupon.usedAt,
            )
    }
}
