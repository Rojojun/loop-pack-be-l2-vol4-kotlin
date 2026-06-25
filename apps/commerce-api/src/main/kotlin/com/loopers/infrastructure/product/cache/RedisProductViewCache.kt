package com.loopers.infrastructure.product.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.config.redis.RedisConfig
import com.loopers.domain.product.ProductView
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisProductViewCache(
    private val redisTemplate: RedisTemplate<String, String>,
    @Qualifier(RedisConfig.REDIS_TEMPLATE_MASTER)
    private val masterTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : ProductViewCache {

    companion object {
        private val TTL: Duration = Duration.ofMinutes(10)
    }

    override fun getViews(ids: List<Long>): Map<Long, ProductView> {
        if (ids.isEmpty()) return emptyMap()
        val keys = ids.map { ProductCacheKeys.viewKey(it) }
        val values = redisTemplate.opsForValue().multiGet(keys) ?: return emptyMap()
        return ids.zip(values).mapNotNull { (id, json) ->
            json?.let { id to objectMapper.readValue(it, ProductView::class.java) }
        }.toMap()
    }

    override fun putAll(views: List<ProductView>) {
        if (views.isEmpty()) return
        // TODO(직접): multiSet 은 TTL 미지원 → 개별 expire(아래) 대신 파이프라인 SETEX 로 묶는 게 효율적.
        val payload = views.associate {
            ProductCacheKeys.viewKey(it.productId) to objectMapper.writeValueAsString(it)
        }
        masterTemplate.opsForValue().multiSet(payload)
        views.forEach { masterTemplate.expire(ProductCacheKeys.viewKey(it.productId), TTL) }
    }

    override fun evict(productId: Long) {
        masterTemplate.delete(ProductCacheKeys.viewKey(productId))
    }
}
