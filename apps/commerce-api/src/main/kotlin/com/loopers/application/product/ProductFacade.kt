package com.loopers.application.product

import com.loopers.domain.brand.BrandService
import com.loopers.domain.like.LikeService
import com.loopers.domain.product.Level
import com.loopers.domain.product.ProductService
import com.loopers.domain.product.assembleForUser
import com.loopers.domain.product.getProductDomainForUser
import com.loopers.domain.product.TechCategory
import com.loopers.domain.stock.StockService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ProductFacade(
    private val productService: ProductService,
    private val brandService: BrandService,
    private val stockService: StockService,
    private val likeService: LikeService,
) {
    fun findProducts(
        brandId: Long?,
        category: TechCategory?,
        level: Level?,
        sort: String,
        pageable: Pageable,
    ): Page<ProductInfo> {
        val productModels = productService.getProducts(brandId, category, level, sort, pageable)
        val productModelIds = productModels.map { it.id }.toList()
        val brandModelIds = productModels.map { it.brandId }.distinct().toList()

        val brandModels = brandService.getBrandsByIds(brandModelIds)
        val stockModels = stockService.getStocksByProductId(productModelIds)
        val likeCounters = likeService.getLikeCountGroupByProductId(productModelIds)

        return productModels
            .map { assembleForUser(it, brandModels, likeCounters, stockModels) }
            .map { ProductInfo.of(it) }
    }

    fun getProduct(productId: Long): ProductInfo {
        val product = productService.getProduct(productId)
        val brand = brandService.getBrandActive(product.brandId)
        val stock = stockService.getStockById(product.id)
        val likeCount = likeService.getLikeCount(product.id)

        return getProductDomainForUser(product, brand, stock, likeCount)
            .let { ProductInfo.of(it) }
    }
}
