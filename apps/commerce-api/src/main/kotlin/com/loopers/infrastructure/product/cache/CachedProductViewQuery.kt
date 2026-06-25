package com.loopers.infrastructure.product.cache

import com.loopers.domain.brand.BrandRepository
import com.loopers.domain.product.Level
import com.loopers.domain.product.ProductModel
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.ProductView
import com.loopers.domain.product.ProductViewPage
import com.loopers.domain.product.ProductViewQuery
import com.loopers.domain.product.TechCategory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.mapNotNull

@Component
class CachedProductViewQuery(
    private val productRepository: ProductRepository,
    private val brandRepository: BrandRepository,
    private val listCache: ProductListCache,
    private val viewCache: ProductViewCache,
    @param:Value("\${product.list.cache.enabled:false}")
    private val cacheEnabled: Boolean,
) : ProductViewQuery {
    private val rebuildLocks = ConcurrentHashMap<String, ReentrantLock>()

    override fun findViewPage(
        brandId: Long?,
        category: TechCategory?,
        level: Level?,
        sort: String,
        pageable: Pageable,
    ): ProductViewPage {
        val key = ProductCacheKeys.normalizeSort(sort)
        val cacheable = cacheEnabled && key != null && brandId == null && category == null && level == null
        if (!cacheable) return fromDatabase(brandId, category, level, sort, pageable)
        val ids = pageIds(key, pageable)
        return ProductViewPage(viewsBy(ids), listCache.size(key))
    }

    private fun pageIds(key: String, pageable: Pageable): List<Long> {
        listCache.getIds(key, pageable.offset, pageable.pageSize.toLong())?.let { return it }
        val lock = rebuildLocks.computeIfAbsent(key) { ReentrantLock() }
        lock.lock()
        try {
            listCache.getIds(key, pageable.offset, pageable.pageSize.toLong())?.let { return it }

            val topIds = productRepository
                .findProducts(null, null, null, key, PageRequest.of(0, CACHE_SIZE))
                .content.map { it.id }
            listCache.rebuild(key, topIds)
            return listCache.getIds(key, pageable.offset, pageable.pageSize.toLong()) ?: emptyList()
        } finally {
            lock.unlock()
        }
    }

    private fun viewsBy(ids: List<Long>): List<ProductView> {
        val cached = viewCache.getViews(ids)
        val missIds = ids.filter { it !in cached }
        val fetched = if (missIds.isEmpty()) emptyList() else findFromDatabase(missIds).also { viewCache.putAll(it) }
        val assembled = cached + fetched.associateBy { it.productId }
        return ids.mapNotNull { assembled[it] }
    }

    private fun toViews(productModels: List<ProductModel>): List<ProductView> {
        val brandIds = productModels.map { it.brandId }.distinct()
        val brandModels = brandRepository.findAllByIdsIn(brandIds).associateBy { it.id }
        return productModels.mapNotNull { productModel -> brandModels[productModel.brandId]?.let { ProductView.of(productModel, it) } }
    }

    private fun findFromDatabase(ids: List<Long>): List<ProductView> = toViews(productRepository.findByIdIn(ids))

    private fun fromDatabase(brandId: Long?, category: TechCategory?, level: Level?, sort: String, pageable: Pageable): ProductViewPage {
        val page = productRepository.findProducts(brandId, category, level, sort, pageable)
        return ProductViewPage(toViews(page.content), page.totalElements)
    }

    companion object {
        private const val CACHE_SIZE = 200
    }
}
