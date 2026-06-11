package com.loopers.domain.like

import com.loopers.domain.BaseEntity

/**
 * LikeRepository 의 InMemory Fake.
 *
 * 운영 JPA 동작을 충실히 반영한다: existsByUserIdAndProductId / findByUserIdAndProductId 는
 * 글로벌 soft-delete 필터(@SQLRestriction)가 없는 운영과 동일하게 deletedAt 여부를 무시하고
 * 전체 row 를 대상으로 한다. (soft-delete 된 row 도 찾아내므로 재등록 시 restore 기반 멱등이 검증된다.)
 * id 부여는 기존 InMemory*Repository 템플릿과 동일하게 BaseEntity 의 id 필드를 리플렉션으로 세팅한다.
 */
class InMemoryLikeRepository : LikeRepository {
    private val data: MutableMap<Long, LikeModel> = HashMap()
    private var sequence = 1L

    override fun save(likeModel: LikeModel): LikeModel {
        val id = BaseEntity::class.java.getDeclaredField("id")
        id.isAccessible = true
        if ((id.get(likeModel) as Long) == 0L) {
            id.set(likeModel, sequence++)
        }
        id.isAccessible = false
        data[likeModel.id] = likeModel
        return likeModel
    }

    /** 테스트 데이터 적재용 별칭 (save 와 동일). */
    fun saveForTest(likeModel: LikeModel): LikeModel = save(likeModel)

    override fun findAllByProductId(productId: Long): List<LikeModel> =
        data.values.filter { it.productId == productId }

    override fun findAllByProductIdIn(productModelIds: List<Long>): List<LikeModel> =
        data.values.filter { it.productId in productModelIds }

    override fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean =
        data.values.any { it.userId == userId && it.productId == productId }

    override fun findByUserIdAndProductId(userId: Long, productId: Long): LikeModel? =
        data.values.firstOrNull { it.userId == userId && it.productId == productId }

    override fun findAllByUserId(userId: Long): List<LikeModel> =
        data.values.filter { it.userId == userId }

    override fun like(userId: Long, productId: Long): LikeResult {
        val existing = data.values.firstOrNull { it.userId == userId && it.productId == productId }
        return when {
            existing != null && existing.available() -> LikeResult.AlreadyLiked   // 이미 active → 멱등 no-op
            existing != null -> { existing.like(); LikeResult.Liked }             // soft-delete된 row 복원
            else -> { save(LikeModel.of(userId, productId)); LikeResult.Liked }    // 신규
        }
    }
}