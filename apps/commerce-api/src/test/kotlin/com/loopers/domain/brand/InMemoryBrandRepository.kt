package com.loopers.domain.brand

import com.loopers.domain.BaseEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class InMemoryBrandRepository : BrandRepository {
    private val data: MutableMap<Long, BrandModel> = HashMap()
    private var sequence = 1L

    override fun save(brandModel: BrandModel): BrandModel {
        val id = sequence++
        try {
            val idField = BaseEntity::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(brandModel, id)
            idField.isAccessible = false
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        data[id] = brandModel
        return brandModel
    }

    override fun findByIdOrNull(brandId: Long): BrandModel? = data[brandId]

    override fun findAll(pageable: Pageable): Page<BrandModel> {
        val content = data.values.toList()
        return PageImpl(content, pageable, content.size.toLong())
    }

    override fun findAllByIdsIn(brandIds: List<Long>): List<BrandModel> =
        data.values.filter { it.id in brandIds }
}
