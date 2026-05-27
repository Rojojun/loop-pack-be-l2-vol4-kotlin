package com.loopers.infrastructure.like

import com.loopers.domain.like.LikeModel
import com.loopers.domain.like.LikeRepository
import org.springframework.stereotype.Component

@Component
class LikeRepositoryImpl(
    private val likeJpaRepository: LikeJpaRepository
) : LikeRepository {
    override fun findAllByProductId(productId: Long): List<LikeModel> =
        likeJpaRepository.findAllByProductId(productId)

    override fun findAllByProductIdIn(productModelIds: List<Long>): List<LikeModel> =
        likeJpaRepository.findAllByProductIdIn(productModelIds)

    override fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean =
        likeJpaRepository.existsByUserIdAndProductId(userId, productId)

    override fun save(likeModel: LikeModel): LikeModel =
        likeJpaRepository.save(likeModel)

    override fun findByUserIdAndProductId(userId: Long, productId: Long): List<LikeModel> =
        likeJpaRepository.findByUserIdAndProductId(userId, productId)

    override fun findAllByUserId(userId: Long): List<LikeModel> =
        likeJpaRepository.findAllByUserId(userId)
}
