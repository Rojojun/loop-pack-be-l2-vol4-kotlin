package com.loopers.infrastructure.product.cache

object ProductCacheKeys {
    private const val LIST_PREFIX = "product:list:"
    private const val VIEW_PREFIX = "product:view:"

    fun normalizeSort(sort: String): String? = when (sort) {
        "", "latest" -> "latest"
        "likes_desc" -> "likes_desc"
        else -> null
    }

    fun listKey(normalizedSort: String): String = "$LIST_PREFIX$normalizedSort"

    fun viewKey(productId: Long): String = "$VIEW_PREFIX$productId"
}
