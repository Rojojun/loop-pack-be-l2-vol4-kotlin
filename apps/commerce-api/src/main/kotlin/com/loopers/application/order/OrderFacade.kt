package com.loopers.application.order

import com.loopers.domain.coupon.UserCouponService
import com.loopers.domain.coupon.applyCoupon
import com.loopers.domain.order.toOrderItems
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
    private val userCouponService: UserCouponService,
) {
    @Transactional
    fun placeOrder(loginId: String, productQuantityPairs: List<Pair<Long, Int>>, couponId: Long? = null): OrderInfo {
        val user = userService.getByLoginId(loginId)
        val productIds = productQuantityPairs.map { it.first }
        val productsById = productService.getProductsByIds(productIds).associateBy { it.id }

        val items = toOrderItems(productQuantityPairs, productsById)
        val totalAmount = items.sumOf { it.totalPrice() }

        val discountAmount = couponId?.let {
            val userCoupon = userCouponService.getWithLockById(it)
            applyCoupon(userCoupon, user.id, totalAmount, ZonedDateTime.now())
        } ?: 0.0

        val stocksByProducts = stockService.findWithLockByProductIdIn(productIds)
            .associateBy { it.productId }

        productQuantityPairs.forEach { (productId, quantity) ->
            (stocksByProducts[productId] orThrowNotFound "재고 정보를 찾을 수 없습니다 : $productId")
                .let { stockService.reduceStock(it, quantity) }
        }

        val orderModel = orderService.createOrder(user.id, items, couponId, discountAmount)

        return OrderInfo(orderModel.id)
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
            discountAmount = order.discountAmount,
            finalAmount = order.finalAmount,
            status = order.status,
            orderedAt = order.orderedAt,
            items = orderItemInfos,
        )
    }
}
