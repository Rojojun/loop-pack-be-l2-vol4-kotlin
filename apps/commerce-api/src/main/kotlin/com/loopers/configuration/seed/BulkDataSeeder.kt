package com.loopers.configuration.seed

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Profile("bulk")
@Component
class BulkDataSeeder(
    private val seeder: ProductSeeder,
    private val jdbc: JdbcTemplate,
) : CommandLineRunner {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String?) {
        val count = jdbc.queryForObject("SELECT COUNT(*) FROM products", Long::class.java) ?: 0
        if (count >= TARGET_PRODUCTS) {
            log.info("[bulk-seed] 스킵 — 이미 {}건", count)
            return
        }
        seeder.seed(brandCount = 50, productCount = TARGET_PRODUCTS, likeAttempts = 500_000)
    }

    companion object {
        private const val TARGET_PRODUCTS = 100_000
    }
}
