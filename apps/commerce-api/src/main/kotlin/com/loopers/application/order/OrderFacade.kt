package com.loopers.application.order

import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class OrderFacade {
    /**
     * 주문 생성: ProductService 조회 → StockService 차감 → OrderService 생성
     */
    fun placeOrder(loginId: String, productQuantityPairs: List<Pair<Long, Int>>): OrderInfo =
        TODO("UserService 조회 + ProductService + StockService.reduceStock + OrderService.createOrder")

    fun findOrders(loginId: String, startAt: LocalDate, endAt: LocalDate): List<OrderSummaryInfo> =
        TODO("UserService.getByLoginId + OrderService.findOrders")

    fun getOrder(loginId: String, orderId: Long): OrderDetailInfo =
        TODO("UserService.getByLoginId + OrderService.getOrderModel + isOwnedBy 검증")
}