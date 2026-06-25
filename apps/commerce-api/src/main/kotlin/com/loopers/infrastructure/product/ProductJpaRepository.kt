package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductModel
import org.springframework.data.jpa.repository.JpaRepository

interface ProductJpaRepository : JpaRepository<ProductModel, Long> {
    fun findByIdIn(productIds: List<Long>): List<ProductModel>
}