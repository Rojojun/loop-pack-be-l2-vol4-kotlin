package com.loopers.domain.like

interface ProductLikeCountRepository {
    fun findByProductIdIn(productIds: List<Long>): List<ProductLikeCountModel>
}
