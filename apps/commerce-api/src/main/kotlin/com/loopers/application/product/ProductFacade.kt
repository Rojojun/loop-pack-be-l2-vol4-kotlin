package com.loopers.application.product

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ProductFacade {
    fun findProducts(
        brandId: Long?,
        category: String?,
        level: String?,
        sort: String,
        pageable: Pageable,
    ): Page<ProductInfo> = TODO("ProductService.findProducts + 정렬·필터 + ProductInfo 매핑")

    fun getProduct(productId: Long): ProductInfo = TODO("ProductService + BrandService + StockService 조합")
}