package com.loopers.domain.stock

import com.loopers.domain.like.ProductId
import com.loopers.support.function.orThrowNotFound
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class StockService(
    private val stockRepository: StockRepository
) {
    fun getStockById(productId: Long): StockModel =
        stockRepository.findStockByProductId(productId) orThrowNotFound "해당하는 상품이 재고에 없습니다."

    fun findWithLockByProductIdIn(productIds: List<Long>): List<StockModel> =
        stockRepository.findWithLockByProductIdIn(productIds)

    fun reduceStock(stockModel: StockModel, quantity: Int): Unit =
        stockModel.reduce(quantity)

    fun restoreStock(stockModel: StockModel, quantity: Int): Unit =
        stockModel.restore(quantity)

    fun save(productId: Long, quantity: Int): StockModel {
        val stockModel = StockModel.of(productId, quantity)
        return stockRepository.save(stockModel)
    }

    fun getStocksByProductId(productIds: List<Long>): Map<ProductId, StockModel> {
        return stockRepository.findStocksByProductIdIn(productIds)
            .associateBy { ProductId(it.productId) }
    }
}
