package com.loopers.infrastructure.like

import com.loopers.domain.like.LikeModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.ZonedDateTime

interface LikeJpaRepository : JpaRepository<LikeModel, Long> {
    fun findAllByProductId(productId: Long): List<LikeModel>

    fun findAllByProductIdIn(productIds: List<Long>): List<LikeModel>

    fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean

    fun findByUserIdAndProductId(userId: Long, productId: Long): LikeModel?

    fun findAllByUserId(userId: Long): MutableList<LikeModel>

    @Modifying
    @Query(
        value = """                                                                                                                                                                                 
              INSERT INTO likes (user_id, product_id, liked_at, created_at, updated_at)                                                                                                               
              VALUES (:userId, :productId, :now, :now, :now)                                                                                                                                          
              ON DUPLICATE KEY UPDATE deleted_at = NULL                                                                                                                                               
          """,
        nativeQuery = true,
    )
    fun upsert(userId: Long, productId: Long, now: ZonedDateTime): Int
}
