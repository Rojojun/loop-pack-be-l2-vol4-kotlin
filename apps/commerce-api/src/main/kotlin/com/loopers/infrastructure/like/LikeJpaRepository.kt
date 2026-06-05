package com.loopers.infrastructure.like

import com.loopers.domain.like.LikeModel
import org.springframework.data.jpa.repository.JpaRepository

interface LikeJpaRepository : JpaRepository<LikeModel, Long> {
    fun findAllByProductId(productId: Long): List<LikeModel>

    fun findAllByProductIdIn(productIds: List<Long>): List<LikeModel>

    fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean

    fun findByUserIdAndProductId(userId: Long, productId: Long): LikeModel?

    fun findAllByUserId(userId: Long): MutableList<LikeModel>
}
