package com.loopers.domain.like

import com.loopers.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Entity
@Table(name = "likes")
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

    companion object {
        fun of(userId: Long, productId: Long) =
            LikeModel(userId, productId, ZonedDateTime.now())
    }
}
