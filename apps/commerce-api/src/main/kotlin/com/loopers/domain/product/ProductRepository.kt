package com.loopers.domain.product

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductRepository {
    fun findByIdIn(productIds: List<Long>): List<ProductModel>

    fun findById(productId: Long): ProductModel?

    fun save(productModel: ProductModel): ProductModel

    fun findProducts(brandId: Long?, pageable: Pageable): Page<ProductModel>

    fun findProducts(brandId: Long?, category: TechCategory?, level: Level?, sort: String, pageable: Pageable): Page<ProductModel>

    fun findProducts(productIds: List<Long>): List<ProductModel>
}
