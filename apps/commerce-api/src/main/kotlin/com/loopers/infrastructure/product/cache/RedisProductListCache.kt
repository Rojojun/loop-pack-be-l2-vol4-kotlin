package com.loopers.infrastructure.product.cache

import com.loopers.config.redis.RedisConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisProductListCache(
    // @Primary = ReadFrom.REPLICA_PREFERRED
    private val redisTemplate: RedisTemplate<String, String>,
    @Qualifier(RedisConfig.REDIS_TEMPLATE_MASTER)
    private val masterTemplate: RedisTemplate<String, String>,
) : ProductListCache {

    companion object {
        private const val MAX_SIZE: Long = 200

        private val TTL: Duration = Duration.ofSeconds(30)
    }

    override fun getIds(sort: String, offset: Long, limit: Long): List<Long>? {
        val key = ProductCacheKeys.listKey(sort)
        val size = redisTemplate.opsForList().size(key) ?: 0L
        if (size == 0L) return null // 미스. TODO(직접): "0건"을 캐싱하는 negative cache 와 구분 필요
        val end = offset + limit - 1
        return redisTemplate.opsForList().range(key, offset, end)?.map { it.toLong() }
    }

    override fun size(sort: String): Long =
        redisTemplate.opsForList().size(ProductCacheKeys.listKey(sort)) ?: 0L

    override fun rebuild(sort: String, ids: List<Long>) {
        val key = ProductCacheKeys.listKey(sort)
        // TODO(직접): 원자성 — DEL+RPUSH+LTRIM 을 파이프라인/MULTI 로 묶을지 결정.
        //            중간 상태(빈 리스트)가 동시 요청에 노출되면 잘못된 미스 유발 가능.
        masterTemplate.delete(key)
        if (ids.isNotEmpty()) {
            masterTemplate.opsForList().rightPushAll(key, ids.map { it.toString() })
            masterTemplate.opsForList().trim(key, 0, MAX_SIZE - 1)
        }
        masterTemplate.expire(key, TTL)
    }

    override fun pushHead(sort: String, productId: Long) {
        val key = ProductCacheKeys.listKey(sort)
        masterTemplate.opsForList().leftPush(key, productId.toString())
        masterTemplate.opsForList().trim(key, 0, MAX_SIZE - 1)
    }

    override fun remove(sort: String, productId: Long) {
        // LREM count=0 → 일치하는 모든 원소 제거
        masterTemplate.opsForList().remove(ProductCacheKeys.listKey(sort), 0, productId.toString())
    }
}
