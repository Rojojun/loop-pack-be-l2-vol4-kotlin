package com.loopers.domain.brand

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BrandRepository {
    fun findByIdOrNull(brandId: Long): BrandModel?

    fun save(brandModel: BrandModel): BrandModel

    fun findAll(pageable: Pageable): Page<BrandModel>

    fun findAllByIdsIn(brandIds: List<Long>): List<BrandModel>
}
