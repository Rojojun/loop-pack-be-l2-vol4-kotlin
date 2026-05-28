package com.loopers.fixture

import com.loopers.domain.like.LikeModel

data class LikeModelFixture(
    val userId: Long,
    val productId: Long,
) {
    fun toModel(): LikeModel = LikeModel.of(userId, productId)

    companion object {
        fun defaults() = LikeModelFixture(userId = 1L, productId = 1L)

        fun custom(userId: Long, productId: Long) = LikeModelFixture(userId, productId)
    }
}
