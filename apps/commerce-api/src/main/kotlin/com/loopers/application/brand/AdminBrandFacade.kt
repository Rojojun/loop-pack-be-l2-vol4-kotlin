package com.loopers.application.brand

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class AdminBrandFacade {
    fun getBrands(pageable: Pageable): Page<AdminBrandInfo> = TODO("BrandService 조회 + AdminBrandInfo 매핑")

    fun getBrand(brandId: Long): AdminBrandInfo = TODO("BrandService.getBrandModel + AdminBrandInfo.from")

    fun createBrand(name: String): AdminBrandInfo = TODO("BrandService.createBrandModel")

    fun updateBrand(brandId: Long, name: String): AdminBrandInfo = TODO("BrandService.updateBrand")

    fun deleteBrand(brandId: Long): Unit = TODO("Brand cascade soft delete (Product/Stock/Like 조율)")
}
