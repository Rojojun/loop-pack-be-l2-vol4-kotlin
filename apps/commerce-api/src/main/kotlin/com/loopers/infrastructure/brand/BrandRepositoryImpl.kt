package com.loopers.infrastructure.brand

import com.loopers.domain.brand.BrandModel
import com.loopers.domain.brand.BrandRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class BrandRepositoryImpl(
    private val brandJpaRepository: BrandJpaRepository
) : BrandRepository {
    override fun findByIdOrNull(brandId: Long): BrandModel? =
        brandJpaRepository.findByIdOrNull(brandId)

    override fun save(brandModel: BrandModel): BrandModel = brandJpaRepository.save(brandModel)

    override fun findAll(pageable: Pageable): Page<BrandModel> {
        return brandJpaRepository.findAll(pageable)
    }

    override fun findAllByIdsIn(brandIds: List<Long>): List<BrandModel> {
        return brandJpaRepository.findAllByIdIn(brandIds)
    }
}
