package com.loopers.domain.like

interface LikeRepository {
    fun findAllByProductId(productId: Long): List<LikeModel>

    fun findAllByProductIdIn(productModelIds: List<Long>): List<LikeModel>

    fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean

    fun save(likeModel: LikeModel): LikeModel

    fun findByUserIdAndProductId(userId: Long, productId: Long): LikeModel?

    fun findAllByUserId(userId: Long): List<LikeModel>

    fun like(userId: Long, productId: Long): LikeResult
}
