package com.loopers.application.order

import com.loopers.domain.order.InMemoryOrderRepository
import com.loopers.domain.order.OrderService
import com.loopers.domain.product.InMemoryProductRepository
import com.loopers.domain.product.Level
import com.loopers.domain.product.ProductModel
import com.loopers.domain.product.ProductService
import com.loopers.domain.product.ProductStatus
import com.loopers.domain.product.TechCategory
import com.loopers.domain.stock.InMemoryStockRepository
import com.loopers.domain.stock.StockModel
import com.loopers.domain.stock.StockService
import com.loopers.domain.user.InMemoryUserRepository
import com.loopers.domain.user.UserModel
import com.loopers.domain.user.UserService
import com.loopers.domain.value.BirthVO
import com.loopers.domain.value.EmailVO
import com.loopers.support.error.CoreException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

class OrderFacadeTest {
    private val inMemoryUserRepository = InMemoryUserRepository()
    private val inMemoryOrderRepository = InMemoryOrderRepository()
    private val inMemoryProductRepository = InMemoryProductRepository()
    private val inMemoryStockRepository = InMemoryStockRepository()

    private val userService = UserService(inMemoryUserRepository)
    private val orderService = OrderService(inMemoryOrderRepository)
    private val productService = ProductService(inMemoryProductRepository)
    private val stockService = StockService(inMemoryStockRepository)

    private val orderFacade = OrderFacade(
        orderService = orderService,
        stockService = stockService,
        productService = productService,
        userService = userService,
    )

    private fun saveUser(loginId: String = "testId"): UserModel =
        inMemoryUserRepository.save(
            UserModel.of(
                loginId,
                "테스터",
                "test_1234",
                BirthVO(LocalDate.of(1993, 3, 16)),
                EmailVO("test@test.com"),
            ) { it },
        )

    private fun saveProduct(
        name: String,
        price: Double,
        status: ProductStatus = ProductStatus.ACTIVE,
    ): ProductModel {
        val product = ProductModel.of(
            brandId = 1L,
            isbn = "isbn-$name",
            name = name,
            authName = "저자",
            techCategory = TechCategory.BACKEND,
            level = Level.BEGINNER,
            price = price,
            description = "설명",
        )
        if (status != ProductStatus.ACTIVE) {
            product.changeStatus(status)
        }
        return inMemoryProductRepository.save(product)
    }

    private fun saveStock(productId: Long, quantity: Int): StockModel =
        inMemoryStockRepository.save(StockModel.of(productId, quantity))

    @DisplayName("placeOrder 가 성공하면")
    @Nested
    internal inner class PlaceOrderSuccess {
        @DisplayName("주문이 생성되고 재고가 차감된다.")
        @Test
        fun success() {
            // given
            val user = saveUser()
            val product1 = saveProduct("테스트 상품1", 1000.0)
            val product2 = saveProduct("테스트 상품2", 2000.0)
            saveStock(product1.id, 10)
            saveStock(product2.id, 5)

            val pairs = listOf(product1.id to 2, product2.id to 3)

            // when
            val orderInfo = orderFacade.placeOrder(user.loginId, pairs)

            // then
            assertNotNull(orderInfo)
            val savedOrder = inMemoryOrderRepository.findByIdOrNull(orderInfo.orderId)
            assertNotNull(savedOrder)
            assertThat(savedOrder!!.userId).isEqualTo(user.id)
            assertThat(savedOrder.items).hasSize(2)
            assertThat(savedOrder.totalPrice()).isEqualTo(1000.0 * 2 + 2000.0 * 3)
            assertThat(inMemoryStockRepository.findStockByProductId(product1.id)!!.quantity).isEqualTo(8)
            assertThat(inMemoryStockRepository.findStockByProductId(product2.id)!!.quantity).isEqualTo(2)
        }
    }

    @DisplayName("placeOrder 시 유저가 존재하지 않으면")
    @Nested
    internal inner class UserNotFound {
        @DisplayName("NOT_FOUND CoreException 을 던진다.")
        @Test
        fun userNotFound() {
            // given
            val product = saveProduct("테스트 상품1", 1000.0)
            saveStock(product.id, 10)
            val pairs = listOf(product.id to 1)

            // when then
            assertThatThrownBy { orderFacade.placeOrder("unknownLoginId", pairs) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("유저의 아이디가 존재하지 않습니다.")
        }
    }

    @DisplayName("placeOrder 시 상품이 유효하지 않으면")
    @Nested
    internal inner class ProductInvalid {
        @DisplayName("존재하지 않는 상품이 포함되면 NOT_FOUND CoreException 을 던진다.")
        @Test
        fun productNotFound() {
            // given
            val user = saveUser()
            val pairs = listOf(999L to 1)

            // when then
            assertThatThrownBy { orderFacade.placeOrder(user.loginId, pairs) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("존재하지 않는 상품이 포함되어 있습니다.")
        }

        @DisplayName("ACTIVE 가 아닌 상품이 포함되면 BAD_REQUEST CoreException 을 던진다.")
        @Test
        fun productNotActive() {
            // given
            val user = saveUser()
            val product = saveProduct("삭제된 상품", 1000.0, ProductStatus.DELETED)
            saveStock(product.id, 10)
            val pairs = listOf(product.id to 1)

            // when then
            assertThatThrownBy { orderFacade.placeOrder(user.loginId, pairs) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("판매 중인 상품이 아닙니다: ${product.id}")
        }
    }

    @DisplayName("placeOrder 시 재고가 부족하면")
    @Nested
    internal inner class StockShortage {
        @DisplayName("BAD_REQUEST CoreException 을 던진다.")
        @Test
        fun stockShortage() {
            // given
            val user = saveUser()
            val product = saveProduct("재고부족 상품", 1000.0)
            saveStock(product.id, 1)
            val pairs = listOf(product.id to 5)

            // when then
            assertThatThrownBy { orderFacade.placeOrder(user.loginId, pairs) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("재고가 부족합니다.")
        }

        @DisplayName("재고 정보 자체가 없으면 NOT_FOUND CoreException 을 던진다.")
        @Test
        fun stockNotFound() {
            // given
            val user = saveUser()
            val product = saveProduct("재고없음 상품", 1000.0)
            // 재고 미저장
            val pairs = listOf(product.id to 1)

            // when then
            assertThatThrownBy { orderFacade.placeOrder(user.loginId, pairs) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("재고 정보를 찾을 수 없습니다 : ${product.id}")
        }
    }
}
