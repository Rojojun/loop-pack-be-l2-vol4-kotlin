package com.loopers.domain.order

import com.loopers.support.error.CoreException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class OrderModelTest {
    @DisplayName("OrderModel을 만들 때 아래의 흐름으로 저장된다.")
    @TestFactory
    fun createOrderModel(): Collection<DynamicTest> {
        lateinit var orderItemModel1: OrderItemModel
        lateinit var orderItemModel2: OrderItemModel
        lateinit var orderModel: OrderModel

        return listOf(
            DynamicTest.dynamicTest("1. OrderItemModel을 먼저 만들고, 만들어진 OrderItemModel의 Order는 null 이다.") {
                // given
                val firstProductId = 1L
                val firstQuantity = 1

                val secondProductId = 2L
                val secondQuantity = 2

                // when
                orderItemModel1 = OrderItemModel.of(firstProductId, "테스트 상품1", 1000.0, firstQuantity)
                orderItemModel2 = OrderItemModel.of(secondProductId, "테스트 상품2", 2000.0, secondQuantity)

                // then
                assertThat(orderItemModel1.order).isNull()
                assertThat(orderItemModel2.order).isNull()
            },
            DynamicTest.dynamicTest("2. OrderItemModel을 사용해서 OrderModel을 생성한다.") {
                // given
                val userId = 1L
                val orderItems = listOf(orderItemModel1, orderItemModel2)

                // when
                orderModel = OrderModel.of(userId, orderItems)

                // then
                assertThat(orderModel).isNotNull()
            },
            DynamicTest.dynamicTest("3. 생성된 OrderItemModel의 OrderModel은 null이 아니다.") {
                assertThat(orderItemModel1.order).isNotNull()
                assertThat(orderItemModel2.order).isNotNull()
                assertThat(orderItemModel1.order).isEqualTo(orderModel)
                assertThat(orderItemModel2.order).isEqualTo(orderModel)
            },
        )
    }

    @DisplayName("OrderItem이 빈 list일 경우 Spec에러를 반환한다.")
    @Test
    fun createOrderModelThrow() {
        // given
        val userId = 1L

        // when then
        assertThatThrownBy { OrderModel.of(userId, emptyList()) }
            .isInstanceOf(CoreException::class.java)
            .hasMessage("주문 항목은 1개 이상이어야 합니다.")
    }

    @DisplayName("UserId가 같은 주문은 취소할 수 있다.")
    @Test
    fun cancelOrderSuccess() {
        // given
        val userId = 1L
        val items = listOf(OrderItemModel.of(1L, "상품", 1000.0, 2))
        val order = OrderModel.of(userId, items)

        // when
        order.cancel(userId)

        // then
        assertThat(order.status).isEqualTo(OrderStatus.CANCELLED)
        assertThat(order.deletedAt).isNotNull()
    }

    @DisplayName("다른 userId 가 취소하면 FORBIDDEN CoreException 을 던진다.")
    @Test
    fun cancelByOtherThrows() {
        // given
        val order = OrderModel.of(1L, listOf(OrderItemModel.of(1L, "상품", 1000.0, 1)))

        // when then
        assertThatThrownBy { order.cancel(999L) }
            .isInstanceOf(CoreException::class.java)
            .hasMessage("본인의 주문만 접근할 수 있습니다.")
    }

    @DisplayName("이미 취소된(PENDING 이 아닌) 주문을 다시 취소하면 CoreException 을 던진다.")
    @Test
    fun cancelNonPendingThrows() {
        // given
        val order = OrderModel.of(1L, listOf(OrderItemModel.of(1L, "상품", 1000.0, 1)))
        order.cancel(1L)

        // when then
        assertThatThrownBy { order.cancel(1L) }
            .isInstanceOf(CoreException::class.java)
            .hasMessage("PENDING 상태의 주문만 취소할 수 있습니다.")
    }
}
