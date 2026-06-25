package com.loopers.infrastructure.product

import com.loopers.domain.like.LikeModel
import com.loopers.domain.like.LikeRepository
import com.loopers.domain.product.Level
import com.loopers.domain.product.ProductModel
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.TechCategory
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest

/**
 * 상품 정렬(A2) 통합 테스트. QueryDSL 정렬은 실제 DB 가 필요하므로 @SpringBootTest 로 검증한다.
 *
 * 중요: 저장 순서(p1 → p2 → p3)와 각 정렬의 기대 순서를 일부러 모두 어긋나게 구성한다.
 * 저장 순서와 기대 순서가 같으면 sort 가 미구현이어도 우연히 통과하는 false positive 가 생기기 때문이다.
 *
 * 핵심 검증:
 *  - likes_desc 가 좋아요 수 내림차순으로 정렬한다. (좋아요 0개 상품도 leftJoin 으로 맨 뒤 포함)
 *  - 취소(soft delete)된 좋아요는 집계에서 제외된다 (ON 절 deletedAt 필터).
 *  - price_asc 가 가격 오름차순으로 정렬한다.
 */
@SpringBootTest
internal class ProductQueryRepositoryIntegrationTest @Autowired constructor(
    private val productRepository: ProductRepository,
    private val likeRepository: LikeRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {
    // 저장 순서: p1 → p2 → p3 (어느 정렬 기대 순서와도 일치하지 않는다)
    private var p1MidId = 0L   // 좋아요 1개, price 2000
    private var p2TopId = 0L   // 좋아요 3개, price 1000
    private var p3ZeroId = 0L  // 좋아요 0개, price 3000
    private var userSeq = 0L

    @BeforeEach
    fun setUp() {
        p1MidId = productRepository.save(product("중간좋아요", price = 2000.0)).id
        p2TopId = productRepository.save(product("최다좋아요", price = 1000.0)).id
        p3ZeroId = productRepository.save(product("좋아요없음", price = 3000.0)).id

        addLikes(p1MidId, 1)
        addLikes(p2TopId, 3)
        // p3ZeroId 는 좋아요 없음
    }

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    @DisplayName("likes_desc: 좋아요 많은 순으로 정렬되고, 좋아요 0개 상품도 맨 뒤에 포함된다.")
    @Test
    fun sortByLikesDesc() {
        val page = productRepository.findProducts(null, null, null, "likes_desc", PageRequest.of(0, 10))

        // 저장 순서(p1, p2, p3)와 다른 (p2, p1, p3) 를 기대 → sort 미구현이면 RED
        assertThat(page.content.map { it.id })
            .containsExactly(p2TopId, p1MidId, p3ZeroId)
    }

    @DisplayName("likes_desc: 취소(soft delete)된 좋아요는 집계에서 제외된다.")
    @Test
    fun softDeletedLikeExcludedFromOrdering() {
        // given : p3(좋아요 0) 에 좋아요 2개를 넣고 모두 취소 → 집계상 여전히 0 이어야 한다
        val deleted1 = likeRepository.save(LikeModel.of(++userSeq, p3ZeroId))
        val deleted2 = likeRepository.save(LikeModel.of(++userSeq, p3ZeroId))
        deleted1.delete()
        deleted2.delete()
        likeRepository.save(deleted1)
        likeRepository.save(deleted2)

        // when
        val page = productRepository.findProducts(null, null, null, "likes_desc", PageRequest.of(0, 10))

        // then : 취소분은 무시되어 p2(3) > p1(1) > p3(0) 순 유지
        assertThat(page.content.map { it.id })
            .containsExactly(p2TopId, p1MidId, p3ZeroId)
    }

    @DisplayName("price_asc: 가격 오름차순으로 정렬된다.")
    @Test
    fun sortByPriceAsc() {
        val page = productRepository.findProducts(null, null, null, "price_asc", PageRequest.of(0, 10))

        // 저장 순서 가격(2000, 1000, 3000)과 다른 오름차순 → sort 미구현이면 RED
        assertThat(page.content.map { it.price })
            .containsExactly(1000.0, 2000.0, 3000.0)
    }

    private fun product(name: String, price: Double) = ProductModel.of(
        brandId = 1L,
        isbn = "isbn-$name",
        name = name,
        authName = "저자",
        techCategory = TechCategory.BACKEND,
        level = Level.BEGINNER,
        price = price,
        description = "설명",
    )

    private fun addLikes(productId: Long, count: Int) {
        repeat(count) { likeRepository.save(LikeModel.of(++userSeq, productId)) }
    }
}
