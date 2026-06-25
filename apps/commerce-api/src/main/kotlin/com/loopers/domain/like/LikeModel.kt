package com.loopers.domain.like

import com.loopers.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.ZonedDateTime

@Entity
@Table(
    name = "likes",
    uniqueConstraints = [UniqueConstraint(name = "uk_like_user_product", columnNames = ["user_id", "product_id"])],
    indexes = [Index(name = "idx_likes_product", columnList = "product_id, deleted_at")],
)
class LikeModel private constructor(
    userId: Long,
    productId: Long,
    likedAt: ZonedDateTime,
) : BaseEntity() {
    var userId: Long = userId
        protected set

    var productId: Long = productId
        protected set

    var likedAt: ZonedDateTime = likedAt
        protected set

    fun available(): Boolean {
        return deletedAt == null
    }

    fun like() {
        restore()
    }

    companion object {
        fun of(userId: Long, productId: Long) =
            LikeModel(userId, productId, ZonedDateTime.now())
    }
}
