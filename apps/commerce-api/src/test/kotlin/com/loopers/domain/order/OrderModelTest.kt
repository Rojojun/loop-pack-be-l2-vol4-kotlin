package com.loopers.domain.order

import com.loopers.support.error.CoreException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class OrderModelTest {
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
                orderItemModel1 = OrderItemModel.of(firstProductId, firstQuantity)
                orderItemModel2 = OrderItemModel.of(secondProductId, secondQuantity)

                // then
                Assertions.assertNull(orderItemModel1.order)
                Assertions.assertNull(orderItemModel2.order)
            },
            DynamicTest.dynamicTest("2. OrderItemModel을 사용해서 OrderModel을 생성한다.") {
                // given
                val userId = 1L
                val orderItems = listOf(orderItemModel1, orderItemModel2)

                // when
                orderModel = OrderModel.of(userId, orderItems)

                // then
                Assertions.assertNotNull(orderModel)
            },
            DynamicTest.dynamicTest("3. 생성된 OrderItemModel의 OrderModel은 null이 아니다.") {
                Assertions.assertNotNull(orderItemModel1.order)
                Assertions.assertNotNull(orderItemModel2.order)
                Assertions.assertEquals(orderModel, orderItemModel1.order)
                Assertions.assertEquals(orderModel, orderItemModel2.order)
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
}
