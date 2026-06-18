package com.loopers.infrastructure.product.cache

import com.loopers.domain.product.ProductView

interface ProductViewCache {

    /** MGET. 미스인 id 는 결과 맵에서 빠진다(자가 치유는 호출부가 DB fallback 으로 채움). */
    fun getViews(ids: List<Long>): Map<Long, ProductView>

    /** DB fallback 으로 채운 view 들을 적재한다. */
    fun putAll(views: List<ProductView>)

    /** 가격/정보 변경 시 해당 view 만 무효화. */
    fun evict(productId: Long)
}
