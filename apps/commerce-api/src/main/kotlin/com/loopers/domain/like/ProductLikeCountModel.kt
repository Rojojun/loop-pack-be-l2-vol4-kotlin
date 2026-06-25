package com.loopers.domain.like

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Entity
@Table(
    name = "product_like_count",
    indexes = [Index(name = "idx_product_like_count_like_count", columnList = "like_count")]
)
class ProductLikeCountModel(
    @Id
    val productId: Long,
    val likeCount: Int,
    val refreshedAt: ZonedDateTime,
)
