package com.loopers.domain.order

import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class OrderServiceTest @Autowired constructor(
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

        val orderItemModels = listOf(OrderItemModel.of(1L, "테스트 상품1", 1000.0, 2), OrderItemModel.of(2L, "테스트 상품2", 2000.0, 2))
        // when
        val orderModel = orderService.createOrder(userId, orderItemModels)

        // then
        assertThat(orderModel.items.size).isEqualTo(orderItemModels.size)
        assertThat(orderModel.items.first()).isEqualTo(orderItemModels.first())
    }

    @DisplayName("OrderModel을 지울경우 OrderModelItem 까지 한 번에 삭제가 된다.")
    @Test
    fun deleteOrderSuccess() {
        // given
        val userId = 1L
        val orderItemModels = listOf(OrderItemModel.of(1L, "테스트 상품1", 1000.0, 2), OrderItemModel.of(2L, "테스트 상품2", 2000.0, 2))
        val orderModel = OrderModel.of(userId, orderItemModels)
            .let { orderRepository.save(it) }
        assertThat(orderModel.deletedAt).isNull()

        // when
        orderService.deleteOrder(orderModel.id, userId)

        // then
        val refreshed = orderRepository.findByIdOrNull(orderModel.id)!!
        assertThat(refreshed.deletedAt).isNotNull()
        assertThat(refreshed.status).isEqualTo(OrderStatus.CANCELLED)
    }
}
