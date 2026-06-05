package com.loopers.domain.coupon

import com.loopers.domain.BaseEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Entity
@Table(name = "user_coupon")
class UserCouponModel private constructor(
    coupon: CouponModel,
    userId: Long,
    status: CouponStatus,
    issuedAt: ZonedDateTime,
): BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    var coupon: CouponModel = coupon
        protected set

    var userId: Long = userId
        protected set

    var status: CouponStatus = status
        protected set

    var issuedAt: ZonedDateTime = issuedAt
        protected set

    var usedAt: ZonedDateTime? = null
        protected set

    fun statusAt(now: ZonedDateTime): CouponStatus = when {
        this.status == CouponStatus.USED -> CouponStatus.USED
        coupon.isExpired(now) -> CouponStatus.EXPIRED
        else -> CouponStatus.AVAILABLE
    }

    fun use() {
        if (status != CouponStatus.AVAILABLE) {
            throw CoreException(ErrorType.CONFLICT, "이미 사용한 쿠폰입니다.")
        }
        this.status = CouponStatus.USED
        this.usedAt = ZonedDateTime.now()
    }

    fun isUsable(userId: Long): Boolean =
        this.userId == userId && this.status == CouponStatus.AVAILABLE

    companion object {
        fun of(coupon: CouponModel, userId: Long): UserCouponModel =
            UserCouponModel(
                coupon = coupon,
                userId = userId,
                status = CouponStatus.AVAILABLE,
                issuedAt = ZonedDateTime.now(),
            )
    }
}
