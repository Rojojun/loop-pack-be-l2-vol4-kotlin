package com.loopers.domain.order

import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OrderServiceTest @Autowired constructor(
    private val orderService: OrderService,
    private val orderRepository: OrderRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {
    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    @DisplayName("OrderModel을 저장하면 OrderModelItem 까지 한 번에 저장이 된다.")
    @Test
    fun createOrderSuccess() {
        // given
        val userId = 1L

        val orderItemModels = listOf(OrderItemModel.of(1L, 2), OrderItemModel.of(2L, 2))
        // when
        val orderModel = orderService.createOrder(userId, orderItemModels)

        // then
        Assertions.assertThat(orderModel.items.size).isEqualTo(orderItemModels.size)
        Assertions.assertThat(orderModel.items.first()).isEqualTo(orderItemModels.first())
    }
}
