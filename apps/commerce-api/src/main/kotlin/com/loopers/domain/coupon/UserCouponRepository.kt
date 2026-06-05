package com.loopers.domain.coupon

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserCouponRepository {
    fun findAllByCouponId(couponId: Long): List<UserCouponModel>

    fun findAllByCouponId(couponId: Long, pageable: Pageable): Page<UserCouponModel>

    fun findByUserId(userId: Long): List<UserCouponModel>

    fun save(userCouponModel: UserCouponModel): UserCouponModel
}
