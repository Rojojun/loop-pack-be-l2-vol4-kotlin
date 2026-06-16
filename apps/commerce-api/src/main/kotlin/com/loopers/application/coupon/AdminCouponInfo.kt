package com.loopers.application.coupon

import com.loopers.domain.coupon.CouponModel
import com.loopers.domain.coupon.CouponType
import java.time.ZonedDateTime

data class AdminCouponInfo private constructor (
    val id: Long,
    val name: String,
    val type: CouponType,
    val value: Double,
    val minOrderAmount: Double,
    val expiredAt: ZonedDateTime,
) {

    companion object {
        fun from(couponModel: CouponModel): AdminCouponInfo =
            AdminCouponInfo(
                id = couponModel.id,
                name = couponModel.name,
                type = couponModel.type,
                value = couponModel.value,
                minOrderAmount = couponModel.minOrderAmount,
                expiredAt = couponModel.expiredAt
            )
    }
}
