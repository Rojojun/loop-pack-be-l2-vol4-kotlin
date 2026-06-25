package com.loopers.infrastructure.product

import com.loopers.domain.product.Level
import com.loopers.domain.product.ProductModel
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.TechCategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ProductRepositoryImpl(
    private val productJpaRepository: ProductJpaRepository,
    private val productQueryRepository: ProductQueryRepository
) : ProductRepository {
    override fun findByIdIn(productIds: List<Long>): List<ProductModel> =
        productJpaRepository.findByIdIn(productIds)

    override fun findById(productId: Long): ProductModel? =
        productJpaRepository.findByIdOrNull(productId)

    override fun save(productModel: ProductModel): ProductModel =
        productJpaRepository.save(productModel)

    override fun findProducts(brandId: Long?, pageable: Pageable): Page<ProductModel> =
        productQueryRepository.findProducts(brandId, pageable)

    override fun findProducts(brandId: Long?, category: TechCategory?, level: Level?, sort: String, pageable: Pageable): Page<ProductModel> =
        productQueryRepository.findProducts(brandId, category, level, sort, pageable)

    override fun findProducts(productIds: List<Long>): List<ProductModel> =
        productJpaRepository.findAllById(productIds)
}
