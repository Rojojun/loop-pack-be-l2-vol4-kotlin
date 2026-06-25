package com.loopers.infrastructure.stock

import com.loopers.domain.stock.StockModel
import com.loopers.domain.stock.StockRepository
import org.springframework.stereotype.Component

@Component
class StockRepositoryImpl(
    private val stockJpaRepository: StockJpaRepository
) : StockRepository {
    override fun findStockByProductId(productId: Long): StockModel? =
        stockJpaRepository.findStockByProductId(productId)

    override fun findStocksByProductIdIn(productIds: List<Long>): List<StockModel> =
        stockJpaRepository.findStocksByProductIdIn(productIds)

    override fun findWithLockByProductIdIn(productIds: List<Long>): List<StockModel> =
        stockJpaRepository.findWithLockByProductIdIn(productIds)

    override fun save(stockModel: StockModel): StockModel {
        return stockJpaRepository.save(stockModel)
    }
}
