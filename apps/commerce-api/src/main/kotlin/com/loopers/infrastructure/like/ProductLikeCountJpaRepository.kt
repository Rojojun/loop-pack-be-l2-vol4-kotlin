package com.loopers.infrastructure.like

import com.loopers.domain.like.ProductLikeCountModel
import org.springframework.data.jpa.repository.JpaRepository

interface ProductLikeCountJpaRepository : JpaRepository<ProductLikeCountModel, Long> {
    fun findByProductIdIn(productIds: List<Long>): List<ProductLikeCountModel>
}