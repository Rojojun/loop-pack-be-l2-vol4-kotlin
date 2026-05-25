package com.loopers.application.product

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class AdminProductFacade {
    fun findProducts(brandId: Long?, pageable: Pageable): Page<AdminProductInfo> =
        TODO("ProductService.findAllByFilter + StockService 조합")

    fun getProduct(productId: Long): AdminProductInfo =
        TODO("ProductService.getProductModel + StockService.getStockModelByProductId 조합")

    fun createProduct(
        brandId: Long,
        isbn: String,
        name: String,
        author: String,
        category: String,
        level: String,
        price: Int,
        initialQuantity: Int,
    ): AdminProductInfo = TODO("Brand ACTIVE 확인 → Product 생성 → Stock 함께 생성 (단일 트랜잭션)")

    fun updateProduct(
        productId: Long,
        name: String,
        author: String,
        category: String,
        level: String,
        price: Int,
    ): AdminProductInfo = TODO("ProductService.updateProduct")

    fun deleteProduct(productId: Long): Unit =
        TODO("Product soft delete + Stock cascade soft delete + Like cascade soft delete")
}
