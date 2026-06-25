package com.loopers.domain.product

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.support.function.orThrowNotFound
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class ProductService(
    private val productRepository: ProductRepository,
) {
    @Transactional(readOnly = true)
    fun getProductsByIds(productIds: List<Long>): List<ProductModel> {
        val products = productRepository.findByIdIn(productIds)

        if (products.size != productIds.distinct().size) {
            throw CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품이 포함되어 있습니다.")
        }
        products.forEach {
            if (it.status != ProductStatus.ACTIVE) {
                throw CoreException(ErrorType.BAD_REQUEST, "판매 중인 상품이 아닙니다: ${it.id}")
            }
        }
        return products
    }

    fun getProduct(productId: Long): ProductModel {
        return productRepository.findById(productId) orThrowNotFound "상품이 존재하지 않습니다."
    }

    fun delete(productId: Long): Unit {
        val productModel = getProduct(productId)
        productModel.delete()
        productModel.changeStatus(ProductStatus.DELETED)
    }

    fun save(
        brandId: Long,
        isbn: String,
        name: String,
        author: String,
        category: TechCategory,
        level: Level,
        price: Double,
        description: String
    ): ProductModel {
        val productModel = ProductModel.of(
            brandId = brandId,
            isbn = isbn,
            name = name,
            authName = author,
            techCategory = category,
            level = level,
            price = price,
            description = description
        )
        return productRepository.save(productModel)
    }

    fun getProducts(brandId: Long?, pageable: Pageable): Page<ProductModel> {
        return productRepository.findProducts(brandId, pageable)
    }

    fun getProducts(brandId: Long?, category: TechCategory?, level: Level?, sort: String, pageable: Pageable): Page<ProductModel> {
        return productRepository.findProducts(brandId, category, level, sort, pageable)
    }

    fun getProducts(productIds: List<Long>): List<ProductModel> {
        return productRepository.findProducts(productIds)
    }
}
