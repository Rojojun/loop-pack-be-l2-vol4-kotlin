package com.loopers.application.brand

import com.loopers.domain.brand.BrandService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class AdminBrandFacade(
    private val brandService: BrandService
) {
    fun getBrands(pageable: Pageable): Page<AdminBrandInfo> {
        return brandService.getBrands(pageable)
            .map { AdminBrandInfo(id = it.id, name = it.name, status = it.status) }
    }

    fun getBrand(brandId: Long): AdminBrandInfo {
        val brandModel = brandService.getBrand(brandId)
        return AdminBrandInfo(id = brandModel.id, name = brandModel.name, status = brandModel.status)
    }

    fun createBrand(name: String): AdminBrandInfo {
        val brandModel = brandService.createBrandModel(name)
        return AdminBrandInfo(id = brandModel.id, name = brandModel.name, status = brandModel.status)
    }

    fun updateBrand(brandId: Long, name: String): AdminBrandInfo {
        val brandModel = brandService.updateBrand(brandId, name)
        return AdminBrandInfo(id = brandModel.id, name = brandModel.name, status = brandModel.status)
    }

    fun deleteBrand(brandId: Long) {
        brandService.delete(brandId)
    }
}
