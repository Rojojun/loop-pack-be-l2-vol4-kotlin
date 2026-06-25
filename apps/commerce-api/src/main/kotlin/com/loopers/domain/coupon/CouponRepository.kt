package com.loopers.domain.coupon

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CouponRepository {
    fun save(couponModel: CouponModel): CouponModel

    fun findByIdOrNull(couponId: Long): CouponModel?

    fun findAll(pageable: Pageable): Page<CouponModel>
}
