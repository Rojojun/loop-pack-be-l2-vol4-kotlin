package com.loopers.interfaces.api.order

import com.loopers.application.order.AdminOrderFacade
import com.loopers.interfaces.api.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api-admin/v1/orders")
class OrderAdminV1Controller(
    private val adminOrderFacade: AdminOrderFacade,
) : OrderAdminV1ApiSpec {

    @GetMapping
    override fun findAllOrders(pageable: Pageable): ApiResponse<Page<OrderAdminV1Dto.OrderAdminSummaryResponse>> {
        val page = adminOrderFacade.findAllOrders(pageable)
            .map { OrderAdminV1Dto.OrderAdminSummaryResponse.from(it) }
        return ApiResponse.success(page)
    }

    @GetMapping("/{orderId}")
    override fun getOrder(@PathVariable orderId: Long): ApiResponse<OrderAdminV1Dto.OrderAdminDetailResponse> {
        val info = adminOrderFacade.getOrder(orderId)
        val response = OrderAdminV1Dto.OrderAdminDetailResponse.from(info)
        return ApiResponse.success(response)
    }
}
