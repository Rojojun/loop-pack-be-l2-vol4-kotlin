package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.CouponModel
import com.loopers.domain.coupon.CouponRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class CouponRepositoryImpl(
    private val couponJpaRepository: CouponJpaRepository
) : CouponRepository {
    override fun save(couponModel: CouponModel): CouponModel = couponJpaRepository.save(couponModel)

    override fun findByIdOrNull(couponId: Long): CouponModel? = couponJpaRepository.findByIdOrNull(couponId)

    override fun findAll(pageable: Pageable): Page<CouponModel> = couponJpaRepository.findAll(pageable)
}
