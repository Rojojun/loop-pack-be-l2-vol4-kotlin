package com.loopers.infrastructure.like

import com.loopers.domain.like.LikeModel
import com.loopers.domain.like.LikeRepository
import com.loopers.domain.like.LikeResult
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

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

    override fun findByUserIdAndProductId(userId: Long, productId: Long): LikeModel? =
        likeJpaRepository.findByUserIdAndProductId(userId, productId)

    override fun findAllByUserId(userId: Long): List<LikeModel> =
        likeJpaRepository.findAllByUserId(userId)

    override fun like(userId: Long, productId: Long): LikeResult {
        val affected = likeJpaRepository.upsert(userId, productId, ZonedDateTime.now())
        return if (affected != 0) LikeResult.Liked else LikeResult.AlreadyLiked
    }
}
