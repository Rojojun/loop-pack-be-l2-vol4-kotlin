package com.loopers.domain.brand

import com.loopers.application.brand.AdminBrandInfo
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.support.function.orThrowNotFound
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class BrandService(
    private val brandRepository: BrandRepository
) {
    @Transactional(readOnly = true)
    fun getBrands(pageable: Pageable): Page<BrandModel> {
        return brandRepository.findAll(pageable)
    }

    @Transactional(readOnly = true)
    fun getBrandsByIds(brandIds: List<Long>): Map<Long, BrandModel> {
        return brandRepository.findAllByIdsIn(brandIds)
            .associateBy { it.id }
    }

    @Transactional(readOnly = true)
    fun getBrand(brandId: Long) =
        brandRepository.findByIdOrNull(brandId) orThrowNotFound "$brandId 에 해당하는 브랜드가 존재하지 않습니다."

    fun createBrandModel(name: String): BrandModel = brandRepository.save(BrandModel.of(name))

    fun updateBrand(brandId: Long, name: String): BrandModel {
        val brand = getBrand(brandId)
        brand.update(name)
        return brand
    }

    fun delete(brandId: Long) {
        val brand = getBrand(brandId)
        brand.delete()
        brand.statusChange(BrandStatus.DELETED)
    }

    fun getBrandActive(brandId: Long): BrandModel {
        val brand = getBrand(brandId)
        check(brand.status == BrandStatus.ACTIVE) {
            throw CoreException(ErrorType.BAD_REQUEST, "브랜드가 활성화되어 있지 않습니다.")
        }
        return brand
    }
}
