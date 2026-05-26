package com.loopers.domain.product

import com.loopers.domain.brand.BrandModel
import com.loopers.domain.like.LikeCount
import com.loopers.domain.like.ProductId
import com.loopers.domain.stock.StockModel
import com.loopers.support.function.orThrowNotFound
import org.springframework.data.domain.Page

object ProductDomainService {
    fun getProductDomainForAdmin(productModel: ProductModel, stockModel: StockModel, likeCount: Int): ProductDomain {
        val stockQuantity = stockModel.quantity
        return ProductDomain.ofAdmin(productModel, stockQuantity, likeCount)
    }

    fun getProductDomainForUser(productModel: ProductModel, brandModel: BrandModel, stockModel: StockModel, likeCount: Int): ProductDomain =
        ProductDomain.ofUser(productModel, brandModel, likeCount, stockModel)

    fun assemble(
        productPage: Page<ProductModel>,
        stockByProductId: Map<ProductId, StockModel>,
        likeCountByProductId: Map<ProductId, LikeCount>,
    ): Page<ProductDomain> = productPage.map { product ->
        val stock = stockByProductId[ProductId(product.id)] orThrowNotFound "등록되지 않은 재고입니다."
        val likeCount = likeCountByProductId[ProductId(product.id)]?.count ?: 0
        getProductDomainForAdmin(product, stock, likeCount)
    }

    fun assemble(
        productPage: Page<ProductModel>,
        brands: Map<Long, BrandModel>,
        likeCountByProductId: Map<ProductId, LikeCount>,
        stockByProductId: Map<ProductId, StockModel>,
    ): Page<ProductDomain> = productPage.map { product ->
        val brand = brands[product.brandId] orThrowNotFound "등록되지 않은 브랜드입니다."
        val likeCount = likeCountByProductId[ProductId(product.id)]?.count ?: 0
        val stock = stockByProductId[ProductId(product.id)] orThrowNotFound "등록되지 않은 재고입니다."

        getProductDomainForUser(product, brand, stock, likeCount)
    }
}
