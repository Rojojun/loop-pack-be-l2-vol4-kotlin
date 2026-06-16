package com.loopers.application.coupon

import com.loopers.domain.coupon.CouponService
import com.loopers.domain.coupon.UserCouponService
import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class CouponFacade(
    private val couponService: CouponService,
    private val userCouponService: UserCouponService,
    private val userService: UserService
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
}
