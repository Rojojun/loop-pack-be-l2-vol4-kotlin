package com.loopers.domain.like

import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * LikeService 의 @Transactional 경계가 실제 DB 에 반영되는지 검증하는 통합 테스트.
 *
 * 의도적으로 테스트 메서드에 @Transactional 을 걸지 않는다.
 * 만약 테스트가 트랜잭션을 열면 영속성 컨텍스트가 유지되어 LikeService 에 @Transactional 이 없어도
 * dirty checking 이 동작해버려 검증이 무의미해진다.
 * 트랜잭션 없이 호출하여, LikeService 자신의 @Transactional 이 경계를 만들어 soft delete/restore 가
 * flush·commit 되는지를 별도 조회로 확인한다.
 */
@SpringBootTest
internal class LikeServiceIntegrationTest @Autowired constructor(
    private val likeService: LikeService,
    private val likeRepository: LikeRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {
    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    @DisplayName("좋아요 취소 시 soft delete 가 실제 DB 에 영속화된다. (@Transactional dirty checking 검증)")
    @Test
    fun removeSoftDeleteIsPersisted() {
        // given : 좋아요 등록 (커밋)
        val userId = 1L
        val productId = 100L
        likeService.addLike(userId, productId)

        // when : 취소 — LikeService.remove 의 @Transactional 이 경계를 만들어 delete() 가 flush 되어야 한다.
        likeService.remove(userId, productId)

        // then : 새 조회 시 soft delete(deletedAt) 가 반영되어 available == false
        val found = likeRepository.findByUserIdAndProductId(userId, productId)
        assertThat(found).isNotNull
        assertThat(found!!.available()).isFalse
    }

    @DisplayName("좋아요 취소 후 재등록하면 새 row 가 아니라 기존 row 가 복구된다. (restore 영속화)")
    @Test
    fun reAddRestoresExistingRow() {
        // given
        val userId = 1L
        val productId = 200L
        likeService.addLike(userId, productId)
        likeService.remove(userId, productId)

        // when : 재등록 → restore
        val reAdded = likeService.addLike(userId, productId)

        // then : 같은 row 가 복구되어 available == true
        assertThat(reAdded).isEqualTo(LikeResult.Liked)
        val found = likeRepository.findByUserIdAndProductId(userId, productId)
        assertThat(found).isNotNull
        assertThat(found!!.available()).isTrue
    }

    @DisplayName("좋아요 취소가 영속화되어 getLikeCount 에 반영된다.")
    @Test
    fun likeCountReflectsRemoval() {
        // given : 같은 상품에 2명이 좋아요
        val productId = 300L
        likeService.addLike(1L, productId)
        likeService.addLike(2L, productId)

        // when : 1명 취소
        likeService.remove(1L, productId)

        // then : available 만 카운트 → 1
        assertThat(likeService.getLikeCount(productId)).isEqualTo(1)
    }
}
