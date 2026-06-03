package com.loopers.domain.coupon

import com.loopers.support.error.CoreException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class UserCouponModelTest {
    val expiredAt = ZonedDateTime.of(
        LocalDateTime.of(2020, 1, 1, 0, 0, 0),
        ZoneId.of("UTC"),
    )
    private fun coupon(expiredAt: ZonedDateTime = this.expiredAt): CouponModel =
        CouponModel.of("쿠폰", CouponType.RATE, 10.0, 10000.0, expiredAt)

    @DisplayName("이미 사용한 쿠폰에 use()를 호출하면 예외가 발생한다.")
    @Test
    fun useThrowTest() {
        // given
        val userCoupon = UserCouponModel.of(coupon(), 1L)
        
        // when
        userCoupon.use()
        
        // then
        Assertions.assertThatThrownBy { userCoupon.use() }
            .isInstanceOf(CoreException::class.java)
    }
    
    @DisplayName("만료된 쿠폰은 isUsable = false")
    @Test
    fun isUsableFalseTest() {
        // given
        val userCoupon = UserCouponModel.of(coupon(ZonedDateTime.now().minusDays(1)), 1L)

        // when then
        Assertions.assertThat(userCoupon.isUsable(2L)).isFalse
    }
}
