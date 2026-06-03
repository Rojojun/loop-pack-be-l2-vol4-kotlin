package com.loopers.domain.coupon

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class CouponModelTest {
    @DisplayName("정액(FIXED) 쿠폰은 주문 금액과 무관하게 고정 할인 금액을 반환한다")
    @Test
    fun discountFixed() {
        // given
        val coupon = CouponModel.of(
            name = "5000원 할인 쿠폰",
            type = CouponType.FIXED,
            value = 5000.0,
            minOrderAmount = 10000.0,
            expiredAt = ZonedDateTime.of(
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                ZoneId.of("UTC"),
            ),
        )

        // when
        val result1 = coupon.discount(12000.0)
        val result2 = coupon.discount(20000.0)

        // then
        Assertions.assertThat(result1).isEqualTo(5000.0)
        Assertions.assertThat(result1).isEqualTo(result2)
    }

    @DisplayName("정률(RATE) 쿠폰은 주문 금액에 따라 할인 금액을 반환한다")
    @Test
    fun discountRate() {
        // given
        val coupon = CouponModel.of(
            name = "테스트 쿠폰",
            type = CouponType.RATE,
            value = 90.0,
            minOrderAmount = 12000.0,
            expiredAt = ZonedDateTime.of(
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                ZoneId.of("UTC"),
            ),
        )

        // when
        val result1 = coupon.discount(15000.0)
        val result2 = coupon.discount(20000.0)
        val result3 = coupon.discount(12345.0)

        // then
        Assertions.assertThat(result1).isNotEqualTo(result2)
        Assertions.assertThat(result2).isEqualTo(18000.0)
        Assertions.assertThat(result3).isNotEqualTo(11110.5)
    }
}
