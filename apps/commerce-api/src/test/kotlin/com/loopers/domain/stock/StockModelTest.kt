package com.loopers.domain.stock

import com.loopers.support.error.CoreException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.Assertions.assertAll

internal class StockModelTest {

    @DisplayName("StockModel을 생성할 때")
    @Nested
    internal inner class Create {
        @DisplayName("quantity가 0 이상이면 정상적으로 생성된다.")
        @Test
        fun createSuccess() {
            // given
            val productId = 1L
            val quantity = 10

            // when
            val stock = StockModel.of(productId, quantity)

            // then
            assertAll(
                { assertThat(stock).isNotNull() },
                { assertThat(stock.productId).isEqualTo(productId) },
                { assertThat(stock.quantity).isEqualTo(quantity) },
            )
        }

        @DisplayName("quantity가 0이어도 정상적으로 생성된다.")
        @Test
        fun createWithZeroQuantity() {
            // given
            val productId = 1L
            val quantity = 0

            // when
            val stock = StockModel.of(productId, quantity)

            // then
            assertThat(stock.quantity).isEqualTo(0)
        }

        @DisplayName("quantity가 0 미만이면 CoreException을 반환한다.")
        @Test
        fun createFailWhenQuantityNegative() {
            // given
            val productId = 1L
            val quantity = -1

            // when then
            assertThatThrownBy { StockModel.of(productId, quantity) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("재고 수량은 0 이상이어야 합니다.")
        }
    }

    @DisplayName("재고를 차감(reduce)할 때")
    @Nested
    internal inner class Reduce {
        @DisplayName("차감 수량이 현재 재고 이하이면 정상적으로 차감된다.")
        @Test
        fun reduceSuccess() {
            // given
            val stock = StockModel.of(1L, 10)

            // when
            stock.reduce(3)

            // then
            assertThat(stock.quantity).isEqualTo(7)
        }

        @DisplayName("현재 재고를 모두 차감하면 quantity가 0이 된다.")
        @Test
        fun reduceAll() {
            // given
            val stock = StockModel.of(1L, 5)

            // when
            stock.reduce(5)

            // then
            assertThat(stock.quantity).isEqualTo(0)
        }

        @DisplayName("현재 재고보다 큰 수량을 차감하면 CoreException을 반환한다.")
        @Test
        fun reduceFailWhenInsufficient() {
            // given
            val stock = StockModel.of(1L, 3)

            // when then
            assertThatThrownBy { stock.reduce(4) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("재고가 부족합니다.")
        }

        @DisplayName("차감 수량이 0이면 CoreException을 반환한다.")
        @Test
        fun reduceFailWhenZero() {
            // given
            val stock = StockModel.of(1L, 10)

            // when then
            assertThatThrownBy { stock.reduce(0) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("차감 수량은 1 이상이어야 합니다.")
        }

        @DisplayName("차감 수량이 음수이면 CoreException을 반환한다.")
        @Test
        fun reduceFailWhenNegative() {
            // given
            val stock = StockModel.of(1L, 10)

            // when then
            assertThatThrownBy { stock.reduce(-1) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("차감 수량은 1 이상이어야 합니다.")
        }
    }

    @DisplayName("재고를 복구(restore)할 때")
    @Nested
    internal inner class Restore {
        @DisplayName("복구 수량이 1 이상이면 정상적으로 증가한다.")
        @Test
        fun restoreSuccess() {
            // given
            val stock = StockModel.of(1L, 5)

            // when
            stock.restore(3)

            // then
            assertThat(stock.quantity).isEqualTo(8)
        }

        @DisplayName("복구 수량이 0이면 CoreException을 반환한다.")
        @Test
        fun restoreFailWhenZero() {
            // given
            val stock = StockModel.of(1L, 5)

            // when then
            assertThatThrownBy { stock.restore(0) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("복구 수량은 1 이상이어야 합니다.")
        }

        @DisplayName("복구 수량이 음수이면 CoreException을 반환한다.")
        @Test
        fun restoreFailWhenNegative() {
            // given
            val stock = StockModel.of(1L, 5)

            // when then
            assertThatThrownBy { stock.restore(-2) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("복구 수량은 1 이상이어야 합니다.")
        }
    }

    @DisplayName("재고 가용성(isAvailable)을 확인할 때")
    @Nested
    internal inner class IsAvailable {
        @DisplayName("요청 수량이 현재 재고보다 적으면 true를 반환한다.")
        @Test
        fun availableWhenLess() {
            // given
            val stock = StockModel.of(1L, 10)

            // when then
            assertThat(stock.isAvailable(5)).isTrue()
        }

        @DisplayName("요청 수량이 현재 재고와 같으면 true를 반환한다.")
        @Test
        fun availableWhenEqual() {
            // given
            val stock = StockModel.of(1L, 10)

            // when then
            assertThat(stock.isAvailable(10)).isTrue()
        }

        @DisplayName("요청 수량이 현재 재고보다 많으면 false를 반환한다.")
        @Test
        fun notAvailableWhenMore() {
            // given
            val stock = StockModel.of(1L, 10)

            // when then
            assertThat(stock.isAvailable(11)).isFalse()
        }
    }

    @DisplayName("품절 여부(isSoldOut)를 확인할 때")
    @Nested
    internal inner class IsSoldOut {
        @DisplayName("재고가 0이면 true를 반환한다.")
        @Test
        fun soldOutWhenZero() {
            // given
            val stock = StockModel.of(1L, 0)

            // when then
            assertThat(stock.isSoldOut()).isTrue()
        }

        @DisplayName("재고가 1 이상이면 false를 반환한다.")
        @Test
        fun notSoldOutWhenPositive() {
            // given
            val stock = StockModel.of(1L, 1)

            // when then
            assertThat(stock.isSoldOut()).isFalse()
        }
    }

    @DisplayName("재고를 모두 차감하면 품절 상태가 되는 흐름")
    @TestFactory
    fun reduceUntilSoldOut(): Collection<DynamicTest> {
        lateinit var stock: StockModel
        return listOf(
            DynamicTest.dynamicTest("1. 재고 2개로 생성하면 품절이 아니다.") {
                stock = StockModel.of(1L, 2)
                assertThat(stock.isSoldOut()).isFalse()
            },
            DynamicTest.dynamicTest("2. 1개를 차감해도 아직 품절이 아니다.") {
                stock.reduce(1)
                assertAll(
                    { assertThat(stock.quantity).isEqualTo(1) },
                    { assertThat(stock.isSoldOut()).isFalse },
                )
            },
            DynamicTest.dynamicTest("3. 남은 1개를 차감하면 품절이 된다.") {
                stock.reduce(1)
                assertAll(
                    { assertThat(stock.quantity).isEqualTo(0) },
                    { assertThat(stock.isSoldOut()).isTrue },
                    { assertThat(stock.isAvailable(1)).isFalse },
                )
            },
        )
    }
}
