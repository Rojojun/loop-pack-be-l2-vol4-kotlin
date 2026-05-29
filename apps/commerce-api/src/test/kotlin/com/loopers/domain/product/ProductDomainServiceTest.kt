package com.loopers.domain.product

import com.loopers.application.product.ProductInfo
import com.loopers.domain.BaseEntity
import com.loopers.domain.brand.BrandModel
import com.loopers.domain.like.LikeCount
import com.loopers.domain.like.ProductId
import com.loopers.domain.stock.StockModel
import com.loopers.support.error.CoreException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ProductDomainServiceTest {

    private fun assignId(entity: BaseEntity, id: Long): BaseEntity {
        val idField = BaseEntity::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(entity, id)
        idField.isAccessible = false
        return entity
    }

    private fun product(id: Long, brandId: Long, name: String): ProductModel {
        val product = ProductModel.of(
            brandId = brandId,
            isbn = "978-89-$name",
            name = name,
            authName = "저자-$name",
            techCategory = TechCategory.BACKEND,
            level = Level.INTERMEDIATE,
            price = 25000.0,
            description = "$name 설명",
        )
        assignId(product, id)
        return product
    }

    private fun brand(id: Long, name: String): BrandModel {
        val brand = BrandModel.of(name)
        assignId(brand, id)
        return brand
    }

    private fun stock(productId: Long, quantity: Int): StockModel = StockModel.of(productId, quantity)

    @DisplayName("사용자용 assembleForUser 는")
    @Nested
    internal inner class AssembleForUser {

        @DisplayName("브랜드명/좋아요수/품절여부를 조립한 ProductDomain 을 반환한다.")
        @Test
        fun assembleUserDomain() {
            // given
            val product1 = product(id = 1L, brandId = 10L, name = "코틀린인액션")
            val product2 = product(id = 2L, brandId = 20L, name = "이펙티브코틀린")

            val brands = mapOf(
                10L to brand(10L, "한빛미디어"),
                20L to brand(20L, "인사이트"),
            )
            val likeCounts = mapOf(
                ProductId(1L) to LikeCount(5),
                ProductId(2L) to LikeCount(0),
            )
            val stocks = mapOf(
                ProductId(1L) to stock(1L, 3),
                ProductId(2L) to stock(2L, 0),
            )

            // when
            val first = assembleForUser(product1, brands, likeCounts, stocks)
            val second = assembleForUser(product2, brands, likeCounts, stocks)

            // then
            assertThat(first.productId).isEqualTo(1L)
            assertThat(first.brandName).isEqualTo("한빛미디어")
            assertThat(first.likeCount).isEqualTo(5)
            assertThat(first.stockQuantity).isEqualTo(3)
            assertThat(first.soldOut).isFalse()

            assertThat(second.brandName).isEqualTo("인사이트")
            assertThat(second.likeCount).isEqualTo(0)
            assertThat(second.stockQuantity).isEqualTo(0)
            assertThat(second.soldOut).isTrue()
        }

        @DisplayName("좋아요 정보가 없는 상품은 likeCount 0 으로 조립한다.")
        @Test
        fun likeCountDefaultsToZero() {
            // given
            val product1 = product(id = 1L, brandId = 10L, name = "코틀린인액션")
            val brands = mapOf(10L to brand(10L, "한빛미디어"))
            val likeCounts = emptyMap<ProductId, LikeCount>()
            val stocks = mapOf(ProductId(1L) to stock(1L, 7))

            // when
            val domain = assembleForUser(product1, brands, likeCounts, stocks)

            // then
            assertThat(domain.likeCount).isEqualTo(0)
        }

        @DisplayName("조립된 ProductDomain 은 ProductInfo.of 로 변환된다.")
        @Test
        fun convertToProductInfo() {
            // given
            val product1 = product(id = 1L, brandId = 10L, name = "코틀린인액션")
            val brands = mapOf(10L to brand(10L, "한빛미디어"))
            val likeCounts = mapOf(ProductId(1L) to LikeCount(2))
            val stocks = mapOf(ProductId(1L) to stock(1L, 0))

            val domain = assembleForUser(product1, brands, likeCounts, stocks)

            // when
            val info = ProductInfo.of(domain)

            // then
            assertThat(info.productId).isEqualTo(1L)
            assertThat(info.name).isEqualTo("코틀린인액션")
            assertThat(info.brandName).isEqualTo("한빛미디어")
            assertThat(info.likeCount).isEqualTo(2)
            assertThat(info.soldOut).isTrue()
        }

        @DisplayName("등록되지 않은 브랜드면 NOT_FOUND 예외를 던진다.")
        @Test
        fun throwWhenBrandMissing() {
            // given
            val product1 = product(id = 1L, brandId = 99L, name = "코틀린인액션")
            val brands = emptyMap<Long, BrandModel>()
            val likeCounts = mapOf(ProductId(1L) to LikeCount(1))
            val stocks = mapOf(ProductId(1L) to stock(1L, 1))

            // when then
            assertThatThrownBy { assembleForUser(product1, brands, likeCounts, stocks) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("등록되지 않은 브랜드입니다.")
        }

        @DisplayName("등록되지 않은 재고면 NOT_FOUND 예외를 던진다.")
        @Test
        fun throwWhenStockMissing() {
            // given
            val product1 = product(id = 1L, brandId = 10L, name = "코틀린인액션")
            val brands = mapOf(10L to brand(10L, "한빛미디어"))
            val likeCounts = mapOf(ProductId(1L) to LikeCount(1))
            val stocks = emptyMap<ProductId, StockModel>()

            // when then
            assertThatThrownBy { assembleForUser(product1, brands, likeCounts, stocks) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("등록되지 않은 재고입니다.")
        }
    }

    @DisplayName("관리자용 assembleForAdmin 은")
    @Nested
    internal inner class AssembleForAdmin {

        @DisplayName("재고수량/좋아요수를 조립하고 brandName 은 null 이다.")
        @Test
        fun assembleAdminDomain() {
            // given
            val product1 = product(id = 1L, brandId = 10L, name = "코틀린인액션")
            val stocks = mapOf(ProductId(1L) to stock(1L, 4))
            val likeCounts = mapOf(ProductId(1L) to LikeCount(8))

            // when
            val domain = assembleForAdmin(product1, stocks, likeCounts)

            // then
            assertThat(domain.productId).isEqualTo(1L)
            assertThat(domain.stockQuantity).isEqualTo(4)
            assertThat(domain.likeCount).isEqualTo(8)
            assertThat(domain.brandName).isNull()
            assertThat(domain.soldOut).isFalse()
        }

        @DisplayName("좋아요 정보가 없으면 likeCount 0 으로 조립한다.")
        @Test
        fun likeCountDefaultsToZero() {
            // given
            val product1 = product(id = 1L, brandId = 10L, name = "코틀린인액션")
            val stocks = mapOf(ProductId(1L) to stock(1L, 4))
            val likeCounts = emptyMap<ProductId, LikeCount>()

            // when
            val domain = assembleForAdmin(product1, stocks, likeCounts)

            // then
            assertThat(domain.likeCount).isEqualTo(0)
        }

        @DisplayName("등록되지 않은 재고면 NOT_FOUND 예외를 던진다.")
        @Test
        fun throwWhenStockMissing() {
            // given
            val product1 = product(id = 1L, brandId = 10L, name = "코틀린인액션")
            val stocks = emptyMap<ProductId, StockModel>()
            val likeCounts = mapOf(ProductId(1L) to LikeCount(1))

            // when then
            assertThatThrownBy { assembleForAdmin(product1, stocks, likeCounts) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("등록되지 않은 재고입니다.")
        }
    }
}
