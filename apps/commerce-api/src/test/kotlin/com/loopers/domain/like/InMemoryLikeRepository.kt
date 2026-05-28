package com.loopers.domain.like

import com.loopers.domain.BaseEntity

/**
 * LikeRepository 의 InMemory Fake.
 *
 * 운영 JPA 동작을 충실히 반영한다: existsByUserIdAndProductId / findByUserIdAndProductId 는
 * 글로벌 soft-delete 필터(@SQLRestriction)가 없는 운영과 동일하게 deletedAt 여부를 무시하고
 * 전체 row 를 대상으로 한다. (덕분에 soft-delete 후 재등록 등 멱등성 결함이 테스트로 그대로 재현된다.)
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

    override fun findByUserIdAndProductId(userId: Long, productId: Long): List<LikeModel> =
        data.values.filter { it.userId == userId && it.productId == productId }

    override fun findAllByUserId(userId: Long): List<LikeModel> =
        data.values.filter { it.userId == userId }
}