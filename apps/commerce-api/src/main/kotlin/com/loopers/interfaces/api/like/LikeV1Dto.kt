package com.loopers.interfaces.api.like

import com.loopers.application.like.LikedProductInfo
import java.time.LocalDateTime

class LikeV1Dto {
    // TODO: Response 필드를 docs/design/01a-api-spec.md 참고하여 채우세요.

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
                TODO("LikedProductInfo → LikedProductResponse 매핑")
        }
    }
}