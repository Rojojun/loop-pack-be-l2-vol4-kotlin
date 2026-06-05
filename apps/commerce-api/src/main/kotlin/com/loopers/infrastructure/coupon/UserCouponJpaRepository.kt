package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.UserCouponModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserCouponJpaRepository : JpaRepository<UserCouponModel, Long> {
    @Query("SELECT uc FROM UserCouponModel uc JOIN FETCH uc.coupon WHERE uc.userId = :userId")
    fun findAllByCoupon_Id(couponId: Long): List<UserCouponModel>

    fun findAllByCoupon_Id(couponId: Long, pageable: Pageable): Page<UserCouponModel>

    fun findByUserId(userId: Long): List<UserCouponModel>
}
