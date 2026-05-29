package com.loopers.domain.order

import com.loopers.domain.product.Level
import com.loopers.domain.product.ProductModel
import com.loopers.domain.product.TechCategory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class OrderDomainServiceTest {

    @DisplayName("각 (productId, quantity) 를 상품 스냅샷(이름·단가)과 함께 OrderItemModel 로 조립한다.")
    @Test
    fun assemblesSnapshot() {
        // given
        val productsById = mapOf(
            1L to product(name = "코틀린 인 액션", price = 30000.0),
            2L to product(name = "이펙티브 자바", price = 36000.0),
        )
        val pairs = listOf(1L to 2, 2L to 1)

        // when
        val items = toOrderItems(pairs, productsById)

        // then : 입력 순서·수량 유지 + 상품 스냅샷이 채워진다
        assertThat(items).hasSize(2)
        assertThat(items[0].productId).isEqualTo(1L)
        assertThat(items[0].productName).isEqualTo("코틀린 인 액션")
        assertThat(items[0].unitPrice).isEqualTo(30000.0)
        assertThat(items[0].quantity).isEqualTo(2)
        assertThat(items[0].totalPrice()).isEqualTo(60000.0)
        assertThat(items[1].productId).isEqualTo(2L)
        assertThat(items[1].quantity).isEqualTo(1)
    }

    @DisplayName("빈 주문 항목이면 빈 리스트를 반환한다.")
    @Test
    fun emptyWhenNoPairs() {
        val items = toOrderItems(emptyList(), emptyMap())

        assertThat(items).isEmpty()
    }

    @DisplayName("productsById 에 없는 상품이 포함되면 예외가 발생한다.")
    @Test
    fun throwsWhenProductMissing() {
        val productsById = mapOf(1L to product(name = "코틀린 인 액션", price = 30000.0))
        val pairs = listOf(1L to 1, 999L to 1)

        assertThatThrownBy { toOrderItems(pairs, productsById) }
            .isInstanceOf(NoSuchElementException::class.java)
    }

    private fun product(name: String, price: Double): ProductModel = ProductModel.of(
        brandId = 1L,
        isbn = "isbn-$name",
        name = name,
        authName = "저자",
        techCategory = TechCategory.BACKEND,
        level = Level.BEGINNER,
        price = price,
        description = "설명",
    )
}
