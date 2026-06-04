package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.UserCouponModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface UserCouponJpaRepository : JpaRepository<UserCouponModel, Long> {
    fun findAllByCoupon_Id(couponId: Long): List<UserCouponModel>

    fun findAllByCoupon_Id(couponId: Long, pageable: Pageable): Page<UserCouponModel>

    fun findByUserId(userId: Long): List<UserCouponModel>
}
