package com.loopers.application.like

import java.time.LocalDateTime

data class LikedProductInfo(
    // TODO: LikeModel + ProductModel 조합 결과
    val productId: Long,
    val productName: String,
    val category: String,
    val level: String,
    val price: Int,
    val likeCount: Int,
    val likedAt: LocalDateTime,
)
