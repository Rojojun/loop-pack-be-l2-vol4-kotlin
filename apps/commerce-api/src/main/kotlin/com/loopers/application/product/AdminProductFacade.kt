package com.loopers.application.product

import com.loopers.domain.like.LikeService
import com.loopers.domain.product.Level
import com.loopers.domain.product.ProductDomainService
import com.loopers.domain.product.ProductService
import com.loopers.domain.product.TechCategory
import com.loopers.domain.stock.StockService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AdminProductFacade(
    private val productService: ProductService,
    private val stockService: StockService,
    private val likeService: LikeService,
) {
    fun getProducts(brandId: Long?, pageable: Pageable): Page<AdminProductInfo> {
        val productModels = productService.getProducts(brandId, pageable)
        val productModelIds = productModels.map { it.id }.toList()

        val stockModels = stockService.getStocksByProductId(productModelIds)
        val likeCounters = likeService.getLikeCountGroupByProductId(productModelIds)

        return ProductDomainService.assemble(productModels, stockModels, likeCounters)
            .map { AdminProductInfo.of(it) }
    }

    fun getProduct(productId: Long): AdminProductInfo {
        val product = productService.getProduct(productId)
        val stock = stockService.getStockById(productId)
        val likeCount = likeService.getLikeCount(stock.productId)

        val productDomain = ProductDomainService.getProductDomainForAdmin(product, stock, likeCount)

        return AdminProductInfo.of(productDomain)
    }

    @Transactional
    fun createProduct(
        brandId: Long,
        isbn: String,
        name: String,
        author: String,
        category: TechCategory,
        level: Level,
        price: Double,
        description: String,
        initialQuantity: Int,
    ): AdminProductInfo {
        val product = productService.save(brandId, isbn, name, author, category, level, price, description)
        stockService.save(product.id, initialQuantity)
        return AdminProductInfo.of(product, initialQuantity)
    }

    fun updateProduct(
        productId: Long,
        name: String,
        author: String,
        category: TechCategory,
        level: Level,
        price: Double,
    ): AdminProductInfo {
        val product = productService.getProduct(productId)
        val stock = stockService.getStockById(productId)
        val likeCount = likeService.getLikeCount(stock.productId)

        product.update(
            name = name,
            author = author,
            techCategory = category,
            level = level,
            price = price,
        )

        return AdminProductInfo.of(product, stock.quantity, likeCount)
    }

    fun deleteProduct(productId: Long) {
        productService.delete(productId)
    }
}
