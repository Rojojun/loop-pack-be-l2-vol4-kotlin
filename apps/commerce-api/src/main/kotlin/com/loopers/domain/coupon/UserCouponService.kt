package com.loopers.domain.coupon

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class UserCouponService(
    private val userCouponRepository: UserCouponRepository
) {
    fun getUserCouponsBy(couponId: Long) =
        userCouponRepository.findAllByCouponId(couponId)

    fun getByUserId(userId: Long) =
        userCouponRepository.findByUserId(userId)

    fun getAllByCouponId(couponId: Long, pageable: Pageable) =
        userCouponRepository.findAllByCouponId(couponId, pageable)
}
