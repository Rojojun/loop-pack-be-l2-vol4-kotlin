package com.loopers.domain.product

import com.loopers.support.error.CoreException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ProductServiceTest {
    private val inMemoryProductRepository = InMemoryProductRepository()
    private val productService = ProductService(inMemoryProductRepository)

    private fun createProduct(name: String): ProductModel =
        ProductModel.of(
            brandId = 1L,
            isbn = "978-89-$name",
            name = name,
            authName = "테스트 저자",
            techCategory = TechCategory.BACKEND,
            level = Level.BEGINNER,
            price = 10000.0,
            description = "$name 설명",
        )

    @DisplayName("상품 ID 목록으로 상품을 조회할 때")
    @Nested
    internal inner class GetProductsByIds {
        private var savedId1 = 0L
        private var savedId2 = 0L

        @BeforeEach
        fun init() {
            savedId1 = inMemoryProductRepository.save(createProduct("코틀린인액션")).id
            savedId2 = inMemoryProductRepository.save(createProduct("이펙티브코틀린")).id
        }

        @DisplayName("존재하지 않는 상품 ID가 포함되면 NOT_FOUND 예외를 던진다.")
        @Test
        fun throwNotFoundWhenContainsMissingProduct() {
            // given
            val notExistId = 999L
            val productIds = listOf(savedId1, notExistId)

            // when then
            assertThatThrownBy { productService.getProductsByIds(productIds) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("존재하지 않는 상품이 포함되어 있습니다.")
        }

        @DisplayName("ACTIVE 상태가 아닌 상품이 포함되면 BAD_REQUEST 예외를 던진다.")
        @Test
        fun throwBadRequestWhenContainsNonActiveProduct() {
            // given
            val deletedProduct = inMemoryProductRepository.save(createProduct("삭제된상품"))
            deletedProduct.changeStatus(ProductStatus.DELETED)
            val productIds = listOf(savedId1, deletedProduct.id)

            // when then
            assertThatThrownBy { productService.getProductsByIds(productIds) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("판매 중인 상품이 아닙니다: ${deletedProduct.id}")
        }

        @DisplayName("모두 존재하고 ACTIVE 상태이면 상품 목록을 반환한다.")
        @Test
        fun returnProductsWhenAllActiveAndExist() {
            // given
            val productIds = listOf(savedId1, savedId2)

            // when
            val products = productService.getProductsByIds(productIds)

            // then
            assertThat(products).hasSize(2)
            assertThat(products.map { it.id }).containsExactlyInAnyOrder(savedId1, savedId2)
        }
    }
}
