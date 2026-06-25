package com.loopers.domain.coupon

import com.loopers.support.function.orThrowNotFound
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class CouponService(
    private val couponRepository: CouponRepository
) {
    fun createCoupon(name: String, type: CouponType, value: Double, minOrderAmount: Double, expiredAt: ZonedDateTime): CouponModel =
        CouponModel.of(
            name = name,
            type = type,
            value = value,
            minOrderAmount = minOrderAmount,
            expiredAt = expiredAt,
        ).let { couponRepository.save(it) }

    fun getCoupon(couponId: Long): CouponModel =
        couponRepository.findByIdOrNull(couponId) orThrowNotFound "해당하는 쿠폰이 존재하지 않습니다."

    fun getCoupons(pageable: Pageable): Page<CouponModel> =
        couponRepository.findAll(pageable)
}
