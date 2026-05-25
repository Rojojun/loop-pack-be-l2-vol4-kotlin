package com.loopers.interfaces.api.product

import com.loopers.application.product.AdminProductFacade
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api-admin/v1/products")
class ProductAdminV1Controller(
    private val adminProductFacade: AdminProductFacade,
) : ProductAdminV1ApiSpec {

    @GetMapping
    override fun findProducts(
        @RequestParam(required = false) brandId: Long?,
        pageable: Pageable,
    ): ApiResponse<Page<ProductAdminV1Dto.ProductAdminResponse>> {
        val page = adminProductFacade.findProducts(brandId, pageable)
            .map { ProductAdminV1Dto.ProductAdminResponse.from(it) }
        return ApiResponse.success(page)
    }

    @GetMapping("/{productId}")
    override fun getProduct(@PathVariable productId: Long): ApiResponse<ProductAdminV1Dto.ProductAdminResponse> {
        val info = adminProductFacade.getProduct(productId)
        val response = ProductAdminV1Dto.ProductAdminResponse.from(info)
        return ApiResponse.success(response)
    }

    @PostMapping
    override fun createProduct(
        @RequestBody @Valid request: ProductAdminV1Dto.CreateProductRequest,
    ): ApiResponse<ProductAdminV1Dto.ProductAdminResponse> {
        val info = adminProductFacade.createProduct(
            brandId = request.brandId,
            isbn = request.isbn,
            name = request.name,
            author = request.author,
            category = request.category,
            level = request.level,
            price = request.price,
            initialQuantity = request.initialQuantity,
        )
        val response = ProductAdminV1Dto.ProductAdminResponse.from(info)
        return ApiResponse.success(response)
    }

    @PutMapping("/{productId}")
    override fun updateProduct(
        @PathVariable productId: Long,
        @RequestBody @Valid request: ProductAdminV1Dto.UpdateProductRequest,
    ): ApiResponse<ProductAdminV1Dto.ProductAdminResponse> {
        val info = adminProductFacade.updateProduct(
            productId = productId,
            name = request.name,
            author = request.author,
            category = request.category,
            level = request.level,
            price = request.price,
        )
        val response = ProductAdminV1Dto.ProductAdminResponse.from(info)
        return ApiResponse.success(response)
    }

    @DeleteMapping("/{productId}")
    override fun deleteProduct(@PathVariable productId: Long): ApiResponse<Any> {
        adminProductFacade.deleteProduct(productId)
        return ApiResponse.success()
    }
}
