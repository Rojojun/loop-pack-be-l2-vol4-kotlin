package com.loopers.infrastructure.product.cache

import com.loopers.domain.brand.BrandRepository
import com.loopers.domain.product.Level
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.ProductView
import com.loopers.domain.product.TechCategory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

/**
 * read-through 계약 단위 테스트 — 캐시/repository 를 mock 으로 주입해 거동만 검증.
 * (Redis·DB 없이 순수 단위. 실제 적중/직렬화는 통합 테스트 영역)
 */
class CachedProductViewQueryTest {

    private val productRepository = mockk<ProductRepository>(relaxed = true)
    private val brandRepository = mockk<BrandRepository>(relaxed = true)
    private val listCache = mockk<ProductListCache>(relaxed = true)
    private val viewCache = mockk<ProductViewCache>(relaxed = true)

    private fun sut(cacheEnabled: Boolean = true) =
        CachedProductViewQuery(productRepository, brandRepository, listCache, viewCache, cacheEnabled)

    private val pageable = PageRequest.of(0, 20)
    private fun view(id: Long) =
        ProductView(id, "상품-$id", "저자", TechCategory.BACKEND, Level.BEGINNER, 1000.0, 10L, "브랜드")

    @Test
    fun `캐시 적중 시 DB(repository)를 전혀 조회하지 않는다 — 캐시의 본질`() {
        every { listCache.getIds("likes_desc", 0, 20) } returns listOf(1L, 2L, 3L)
        every { viewCache.getViews(listOf(1L, 2L, 3L)) } returns
            mapOf(1L to view(1), 2L to view(2), 3L to view(3))
        every { listCache.size("likes_desc") } returns 3

        val page = sut().findViewPage(null, null, null, "likes_desc", pageable)

        assertThat(page.views.map { it.productId }).containsExactly(1L, 2L, 3L)
        assertThat(page.total).isEqualTo(3)
        verify(exactly = 0) { productRepository.findProducts(any(), any(), any(), any(), any()) }
        verify(exactly = 0) { productRepository.findByIdIn(any()) }
    }

    @Test
    fun `getViews 가 뒤섞인 Map 을 줘도 결과는 id(정렬) 순서를 유지한다`() {
        every { listCache.getIds("likes_desc", 0, 20) } returns listOf(1L, 2L, 3L)
        every { viewCache.getViews(listOf(1L, 2L, 3L)) } returns
            mapOf(3L to view(3), 1L to view(1), 2L to view(2)) // 일부러 섞음
        every { listCache.size("likes_desc") } returns 3

        val page = sut().findViewPage(null, null, null, "likes_desc", pageable)

        assertThat(page.views.map { it.productId }).containsExactly(1L, 2L, 3L)
    }

    @Test
    fun `리스트 캐시 미스면 상위 N 을 DB에서 읽어 rebuild 한다`() {
        every { listCache.getIds("likes_desc", 0, 20) } returnsMany listOf(null, listOf(1L, 2L))
        every { productRepository.findProducts(null, null, null, "likes_desc", PageRequest.of(0, 200)) } returns
            PageImpl(emptyList())
        every { viewCache.getViews(listOf(1L, 2L)) } returns mapOf(1L to view(1), 2L to view(2))
        every { listCache.size("likes_desc") } returns 2

        sut().findViewPage(null, null, null, "likes_desc", pageable)

        verify { listCache.rebuild("likes_desc", any()) }
    }

    @Test
    fun `엔티티 미스면 그 id 만 DB fallback 으로 조회를 시도한다 — 자가 치유`() {
        every { listCache.getIds("likes_desc", 0, 20) } returns listOf(1L, 2L, 3L)
        every { viewCache.getViews(listOf(1L, 2L, 3L)) } returns mapOf(1L to view(1)) // 2,3 미스
        every { productRepository.findByIdIn(listOf(2L, 3L)) } returns emptyList()
        every { listCache.size("likes_desc") } returns 3

        sut().findViewPage(null, null, null, "likes_desc", pageable)

        verify { productRepository.findByIdIn(listOf(2L, 3L)) }
    }

    @Test
    fun `필터가 있으면 캐시를 우회하고 DB 경로로 간다`() {
        every { productRepository.findProducts(1L, null, null, "likes_desc", pageable) } returns PageImpl(emptyList())

        sut().findViewPage(1L, null, null, "likes_desc", pageable)

        verify(exactly = 0) { listCache.getIds(any(), any(), any()) }
        verify { productRepository.findProducts(1L, null, null, "likes_desc", pageable) }
    }

    @Test
    fun `flag 가 OFF 면 노필터 인기순이라도 캐시를 우회한다`() {
        every { productRepository.findProducts(null, null, null, "likes_desc", pageable) } returns PageImpl(emptyList())

        sut(cacheEnabled = false).findViewPage(null, null, null, "likes_desc", pageable)

        verify(exactly = 0) { listCache.getIds(any(), any(), any()) }
    }
}
