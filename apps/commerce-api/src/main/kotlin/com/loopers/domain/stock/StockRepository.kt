package com.loopers.domain.stock

interface StockRepository {
    fun findStockByProductId(productId: Long): StockModel?

    fun findStocksByProductIdIn(productIds: List<Long>): List<StockModel>

    fun save(stockModel: StockModel): StockModel
}
