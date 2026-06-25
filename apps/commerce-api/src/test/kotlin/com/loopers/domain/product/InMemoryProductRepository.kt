package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class InMemoryProductRepository : ProductRepository {
    private val data: MutableMap<Long, ProductModel> = HashMap()
    private var sequence = 1L

    override fun save(productModel: ProductModel): ProductModel {
        val id = sequence++
        try {
            val idField = BaseEntity::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(productModel, id)
            idField.isAccessible = false
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        data[id] = productModel
        return productModel
    }

    override fun findByIdIn(productIds: List<Long>): List<ProductModel> =
        productIds.mapNotNull { data[it] }

    override fun findById(productId: Long): ProductModel? = data[productId]

    override fun findProducts(brandId: Long?, pageable: Pageable): Page<ProductModel> {
        TODO("Not yet implemented")
    }

    override fun findProducts(
        brandId: Long?,
        category: TechCategory?,
        level: Level?,
        sort: String,
        pageable: Pageable,
    ): Page<ProductModel> {
        TODO("Not yet implemented")
    }

    override fun findProducts(productIds: List<Long>): List<ProductModel> =
        productIds.mapNotNull { data[it] }
}
