package com.loopers.domain.stock

import com.loopers.domain.BaseEntity

class InMemoryStockRepository : StockRepository {
    private val data: MutableMap<Long, StockModel> = HashMap()
    private var sequence = 1L

    override fun save(stockModel: StockModel): StockModel {
        val id = sequence++
        try {
            val idField = BaseEntity::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(stockModel, id)
            idField.isAccessible = false
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        data[stockModel.productId] = stockModel
        return stockModel
    }

    override fun findStockByProductId(productId: Long): StockModel? = data[productId]

    override fun findStocksByProductIdIn(productIds: List<Long>): List<StockModel> =
        productIds.mapNotNull { data[it] }
}
