package com.loopers.infrastructure.stock

import com.loopers.domain.stock.StockModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class StockJpaRepositoryTest @Autowired constructor(
    val stockRepository: StockJpaRepository,
) {
    @Transactional
    @DisplayName("비관적락 쿼리 적용 확인 테스트")
    @Test
    fun findWithLockByProductIdInTest() {
        val stock = StockModel.of(productId = 1L, quantity = 10)
        stockRepository.save(stock)

        val value = stockRepository.findWithLockByProductIdIn(listOf(1L))
        Assertions.assertNotNull(value)
    }
}
