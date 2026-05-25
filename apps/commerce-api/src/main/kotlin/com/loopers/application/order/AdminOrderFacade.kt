package com.loopers.application.order

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class AdminOrderFacade {
    fun findAllOrders(pageable: Pageable): Page<AdminOrderSummaryInfo> =
        TODO("OrderService.findAllOrders 페이지 단위 조회 + AdminOrderSummaryInfo 매핑")

    fun getOrder(orderId: Long): AdminOrderDetailInfo =
        TODO("OrderService.getOrderModel + AdminOrderDetailInfo 매핑")
}