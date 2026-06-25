package com.loopers.infrastructure.stock

import com.loopers.domain.stock.StockModel
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock

interface StockJpaRepository : JpaRepository<StockModel, Long> {
    fun findStockByProductId(productId: Long): StockModel?

    fun findStocksByProductIdIn(productIds: List<Long>): List<StockModel>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findWithLockByProductIdIn(productIds: List<Long>): List<StockModel>
}
