package com.loopers.domain.order

import com.loopers.domain.product.ProductModel
import org.springframework.stereotype.Component

@Component
class OrderDomainService {
    fun toOrderItems(
        productQuantityPairs: List<Pair<Long, Int>>,
        productsById: Map<Long, ProductModel>,
    ): List<OrderItemModel> =
        productQuantityPairs.map { (productId, quantity) ->
            val product = productsById.getValue(productId)
            OrderItemModel.of(productId, product.name, product.price, quantity)
        }
}
