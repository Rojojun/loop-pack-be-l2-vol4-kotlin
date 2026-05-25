package com.loopers.application.product

data class AdminProductInfo(
    // TODO: 어드민 노출 필드 (전체 정보 — isbn, stockQuantity, status 포함)
    val productId: Long,
    val brandId: Long,
    val isbn: String,
    val name: String,
    val author: String,
    val category: String,
    val level: String,
    val price: Int,
    val stockQuantity: Int,
    val likeCount: Int,
    val status: String,
)
