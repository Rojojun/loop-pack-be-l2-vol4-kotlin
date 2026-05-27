package com.loopers.interfaces.api.like

import com.loopers.application.like.LikedProductInfo
import java.time.LocalDateTime

class LikeV1Dto {
    data class LikedProductResponse(
        val productId: Long,
        val productName: String,
        val category: String,
        val level: String,
        val price: Int,
        val likeCount: Int,
        val likedAt: LocalDateTime,
    ) {
        companion object {
            fun from(info: LikedProductInfo): LikedProductResponse =
                LikedProductResponse(
                    productId = info.productId,
                    productName = info.productName,
                    category = info.category.name,
                    level = info.level.name,
                    price = info.price.toInt(),
                    likeCount = info.likeCount,
                    likedAt = info.likedAt.toLocalDateTime(),
                )
        }
    }
}
