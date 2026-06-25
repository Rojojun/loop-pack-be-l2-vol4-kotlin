package com.loopers.application.brand

import com.loopers.domain.brand.BrandService
import org.springframework.stereotype.Component

@Component
class BrandFacade(
    private val brandService: BrandService
) {
    fun getBrand(brandId: Long): BrandInfo =
        brandService.getBrand(brandId)
            .let { BrandInfo(id = it.id, name = it.name) }
}
