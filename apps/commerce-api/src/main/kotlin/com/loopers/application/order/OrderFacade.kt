package com.loopers.application.order

import com.loopers.domain.order.OrderItemModel
import com.loopers.domain.order.OrderService
import com.loopers.domain.product.ProductService
import com.loopers.domain.stock.StockService
import com.loopers.domain.user.UserService
import com.loopers.support.function.orThrowNotFound
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Component
class OrderFacade(
    private val orderService: OrderService,
    private val stockService: StockService,
    private val productService: ProductService,
    private val userService: UserService,
) {
    @Transactional
    fun placeOrder(loginId: String, productQuantityPairs: List<Pair<Long, Int>>): OrderInfo {
        val user = userService.getByLoginId(loginId)
        val productIds = productQuantityPairs.map { it.first }
        val productsById = productService.getProductsByIds(productIds).associateBy { it.id }

        val stocksByProducts = stockService.findStocksByProductIdIn(productIds)
            .associateBy { it.productId }

        productQuantityPairs.forEach { (productId, quantity) ->
            (stocksByProducts[productId] orThrowNotFound "재고 정보를 찾을 수 없습니다 : $productId")
                .let { stockService.reduceStock(it, quantity) }
        }

        return productQuantityPairs.map { (productId, quantity) ->
            val product = productsById.getValue(productId)
            OrderItemModel.of(productId, product.name, product.price, quantity) }
            .let { orderService.createOrder(user.id, it) }
            .let { OrderInfo(it.id) }
    }

    @Transactional(readOnly = true)
    fun findOrders(loginId: String, startAt: ZonedDateTime, endAt: ZonedDateTime): List<OrderSummaryInfo> {
        val user = userService.getByLoginId(loginId)
        val orders = orderService.getOrders(startAt, endAt)
            .also { it.forEach { order -> order.validateOwnedBy(user.id) } }

        return orders.map {
            OrderSummaryInfo(
                orderId = it.id,
                totalPrice = it.totalPrice(),
                status = it.status,
                orderedAt = it.orderedAt,
                itemCount = it.items.size,
            )
        }
    }

    @Transactional(readOnly = true)
    fun getOrder(loginId: String, orderId: Long): OrderDetailInfo {
        val user = userService.getByLoginId(loginId)
        val order = orderService.getOrder(orderId)
            .also { it.validateOwnedBy(user.id) }
        val orderItemInfos = order.items.map { OrderItemInfo(
            productId = it.productId,
            productNameSnapshot = it.productName,
            unitPriceSnapshot = it.unitPrice,
            quantity = it.quantity,
            totalPrice = it.totalPrice(),
        ) }

        return OrderDetailInfo(
            orderId = order.id,
            totalPrice = order.totalPrice(),
            status = order.status,
            orderedAt = order.orderedAt,
            items = orderItemInfos,
        )
    }
}
