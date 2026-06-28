package com.loopers.domain.order

import com.loopers.domain.product.ProductModel
import com.loopers.domain.stock.StockModel

fun toOrderItems(
    productQuantityPairs: List<Pair<Long, Int>>,
    productsById: Map<Long, ProductModel>,
): List<OrderItemModel> =
    productQuantityPairs.map { (productId, quantity) ->
        val product = productsById.getValue(productId)
        OrderItemModel.of(productId, product.name, product.price, quantity)
    }

fun restoreStockOnCancel(order: OrderModel, stocks: List<StockModel>) {
    val stockByProductId = stocks.associateBy { it.productId }
    order.items.forEach { item -> stockByProductId[item.productId]?.restore(item.quantity) }
    order.markCancel()
}
