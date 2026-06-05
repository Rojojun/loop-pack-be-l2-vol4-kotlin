package com.loopers.infrastructure.stock

import com.loopers.domain.stock.StockModel
import org.springframework.data.jpa.repository.JpaRepository

interface StockJpaRepository : JpaRepository<StockModel, Long> {
    fun findStockByProductId(productId: Long): StockModel?

    fun findStocksByProductIdIn(productIds: List<Long>): List<StockModel>
}
