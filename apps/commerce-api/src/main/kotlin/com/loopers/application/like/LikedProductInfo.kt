package com.loopers.application.like

import com.loopers.domain.like.LikeCount
import com.loopers.domain.like.LikeModel
import com.loopers.domain.product.Level
import com.loopers.domain.product.ProductModel
import com.loopers.domain.product.TechCategory
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class LikedProductInfo(
    val productId: Long,
    val productName: String,
    val category: TechCategory,
    val level: Level,
    val price: Double,
    val likeCount: Int,
    val likedAt: ZonedDateTime,
) {
    companion object {
        fun of(likeModel: LikeModel, product: ProductModel, likeCount: LikeCount?): LikedProductInfo =
            LikedProductInfo(
                productId = product.id,
                productName = product.name,
                category = product.techCategory,
                level = product.level,
                price = product.price,
                likeCount = likeCount?.count ?: 0,
                likedAt = likeModel.likedAt
            )
    }
}
