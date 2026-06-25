package com.loopers.domain.product

import com.loopers.domain.brand.BrandModel
import com.loopers.domain.like.LikeCount
import com.loopers.domain.like.ProductId
import com.loopers.domain.stock.StockModel
import com.loopers.support.function.orThrowNotFound

fun getProductDomainForAdmin(productModel: ProductModel, stockModel: StockModel, likeCount: Int): ProductDomain {
    val stockQuantity = stockModel.quantity
    return ProductDomain.ofAdmin(productModel, stockQuantity, likeCount)
}

fun getProductDomainForUser(
    productModel: ProductModel,
    brandModel: BrandModel,
    stockModel: StockModel,
    likeCount: Int,
): ProductDomain =
    ProductDomain.ofUser(productModel, brandModel, likeCount, stockModel)

fun assembleForAdmin(
    product: ProductModel,
    stockByProductId: Map<ProductId, StockModel>,
    likeCountByProductId: Map<ProductId, LikeCount>,
): ProductDomain {
    val stock = stockByProductId[ProductId(product.id)] orThrowNotFound "등록되지 않은 재고입니다."
    val likeCount = likeCountByProductId[ProductId(product.id)]?.count ?: 0
    return getProductDomainForAdmin(product, stock, likeCount)
}

fun assembleForUser(
    product: ProductModel,
    brands: Map<Long, BrandModel>,
    likeCountByProductId: Map<ProductId, LikeCount>,
    stockByProductId: Map<ProductId, StockModel>,
): ProductDomain {
    val brand = brands[product.brandId] orThrowNotFound "등록되지 않은 브랜드입니다."
    val likeCount = likeCountByProductId[ProductId(product.id)]?.count ?: 0
    val stock = stockByProductId[ProductId(product.id)] orThrowNotFound "등록되지 않은 재고입니다."
    return getProductDomainForUser(product, brand, stock, likeCount)
}
