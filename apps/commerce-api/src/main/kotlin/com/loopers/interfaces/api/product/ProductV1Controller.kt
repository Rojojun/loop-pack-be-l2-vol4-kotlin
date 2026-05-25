package com.loopers.interfaces.api.product

import com.loopers.application.product.ProductFacade
import com.loopers.interfaces.api.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductV1Controller(
    private val productFacade: ProductFacade,
) : ProductV1ApiSpec {

    @GetMapping
    override fun findProducts(
        @RequestParam(required = false) brandId: Long?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) level: String?,
        @RequestParam(required = false, defaultValue = "latest") sort: String,
        pageable: Pageable,
    ): ApiResponse<Page<ProductV1Dto.ProductResponse>> {
        val page = productFacade.findProducts(brandId, category, level, sort, pageable)
            .map { ProductV1Dto.ProductResponse.from(it) }
        return ApiResponse.success(page)
    }

    @GetMapping("/{productId}")
    override fun getProduct(@PathVariable productId: Long): ApiResponse<ProductV1Dto.ProductResponse> {
        val info = productFacade.getProduct(productId)
        val response = ProductV1Dto.ProductResponse.from(info)
        return ApiResponse.success(response)
    }
}
