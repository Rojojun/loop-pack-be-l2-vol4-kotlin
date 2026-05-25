package com.loopers.interfaces.api.brand

import com.loopers.application.brand.AdminBrandFacade
import com.loopers.interfaces.api.ApiResponse
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api-admin/v1/brands")
class BrandAdminV1Controller(
    private val adminBrandFacade: AdminBrandFacade,
) : BrandAdminV1ApiSpec {

    @GetMapping
    override fun getBrands(pageable: Pageable): ApiResponse<Page<BrandAdminV1Dto.BrandAdminResponse>> {
        val page = adminBrandFacade.getBrands(pageable)
            .map { BrandAdminV1Dto.BrandAdminResponse.from(it) }
        return ApiResponse.success(page)
    }

    @GetMapping("/{brandId}")
    override fun getBrand(@PathVariable brandId: Long): ApiResponse<BrandAdminV1Dto.BrandAdminResponse> {
        val info = adminBrandFacade.getBrand(brandId)
        val response = BrandAdminV1Dto.BrandAdminResponse.from(info)
        return ApiResponse.success(response)
    }

    @PostMapping
    override fun createBrand(
        @RequestBody @Valid request: BrandAdminV1Dto.CreateBrandRequest,
    ): ApiResponse<BrandAdminV1Dto.BrandAdminResponse> {
        val info = adminBrandFacade.createBrand(request.name)
        val response = BrandAdminV1Dto.BrandAdminResponse.from(info)
        return ApiResponse.success(response)
    }

    @PutMapping("/{brandId}")
    override fun updateBrand(
        @PathVariable brandId: Long,
        @RequestBody @Valid request: BrandAdminV1Dto.UpdateBrandRequest,
    ): ApiResponse<BrandAdminV1Dto.BrandAdminResponse> {
        val info = adminBrandFacade.updateBrand(brandId, request.name)
        val response = BrandAdminV1Dto.BrandAdminResponse.from(info)
        return ApiResponse.success(response)
    }

    @DeleteMapping("/{brandId}")
    override fun deleteBrand(@PathVariable brandId: Long): ApiResponse<Any> {
        adminBrandFacade.deleteBrand(brandId)
        return ApiResponse.success()
    }
}