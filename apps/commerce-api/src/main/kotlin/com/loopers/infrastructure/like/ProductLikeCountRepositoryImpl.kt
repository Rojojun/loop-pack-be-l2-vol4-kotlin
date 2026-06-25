package com.loopers.infrastructure.like

import com.loopers.domain.like.ProductLikeCountModel
import com.loopers.domain.like.ProductLikeCountRepository
import org.springframework.stereotype.Repository

@Repository
class ProductLikeCountRepositoryImpl(
    private val productLikeCountJpaRepository: ProductLikeCountJpaRepository,
) : ProductLikeCountRepository {
    override fun findByProductIdIn(productIds: List<Long>): List<ProductLikeCountModel> =
        productLikeCountJpaRepository.findByProductIdIn(productIds)
}
