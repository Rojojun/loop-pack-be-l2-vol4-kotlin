package com.loopers.application.product

data class ProductInfo(
    // TODO: 대고객 노출 필드 정의 (id, name, author, category, level, price, likeCount, brandId, brandName, soldOut)
    val productId: Long,
    val name: String,
    val author: String,
    val category: String,
    val level: String,
    val price: Int,
    val likeCount: Int,
    val brandId: Long,
    val brandName: String,
    val soldOut: Boolean,
)
