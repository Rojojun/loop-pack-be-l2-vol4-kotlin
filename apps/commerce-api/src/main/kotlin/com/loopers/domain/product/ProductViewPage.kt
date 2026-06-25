package com.loopers.domain.product

/**
 * 정렬된 [ProductView] 페이지. 순서를 유지하며 total 은 전체 건수(캐시면 리스트 길이, ADR-0002 D2).
 */
data class ProductViewPage(
    val views: List<ProductView>,
    val total: Long,
)
