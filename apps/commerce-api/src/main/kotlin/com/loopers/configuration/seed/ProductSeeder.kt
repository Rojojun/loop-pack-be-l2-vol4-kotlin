package com.loopers.configuration.seed

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class ProductSeeder(
    private val jdbcTemplate: JdbcTemplate,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun seed(brandCount: Int, productCount: Int, likeAttempts: Int) {
        log.info("[seed] start - brands={}, products={}, likeAttempts={}", brandCount, productCount, likeAttempts)
        clear()
        seedBrands(brandCount)
        seedProducts(productCount, brandCount)
        seedLikes(likeAttempts, productCount)
        log.info("[seed] done")
    }

    private fun clear() {
        jdbcTemplate.execute("TRUNCATE TABLE likes")
        jdbcTemplate.execute("TRUNCATE TABLE products")
        jdbcTemplate.execute("TRUNCATE TABLE brands")
    }

    private fun seedBrands(count: Int) {
        val query = jdbcTemplate.update("""
            INSERT INTO brands (name, status, created_at, updated_at)
            SELECT CONCAT('브랜드-', n), 'ACTIVE', NOW(6), NOW(6)
            FROM (
              SELECT (d2.i * 10 + d1.i) + 1 AS n
              FROM (SELECT 0 i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d1
              CROSS JOIN (SELECT 0 i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d2
            ) t
            WHERE n BETWEEN 1 AND $count;
        """.trimIndent())
        log.info("[seed] brands={}", query)
    }

    private fun seedProducts(count: Int, brandCount: Int) {
        val query = jdbcTemplate.update("""
            INSERT INTO products
              (brand_id, isbn, name, auth_name, tech_category, level, price, status, description, created_at, updated_at)
            SELECT
              1 + (n % $brandCount)              AS brand_id,
              CONCAT('978', LPAD(n, 10, '0'))    AS isbn,
              CONCAT('상품-', n)                  AS name,
              CONCAT('저자-', 1 + (n % 500))      AS auth_name,
              n % 8                              AS tech_category,
              n % 3                              AS level,
              1000 * (5 + (n % 96))              AS price,
              0                                  AS status,
              CONCAT('상품 ', n, ' 에 대한 설명')  AS description,
              NOW(6) - INTERVAL n MINUTE         AS created_at,
              NOW(6) - INTERVAL n MINUTE         AS updated_at
            FROM (
              SELECT (d5.i*10000 + d4.i*1000 + d3.i*100 + d2.i*10 + d1.i) + 1 AS n
              FROM (SELECT 0 i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d1
              CROSS JOIN (SELECT 0 i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d2
              CROSS JOIN (SELECT 0 i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d3
              CROSS JOIN (SELECT 0 i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d4
              CROSS JOIN (SELECT 0 i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d5
            ) t
            WHERE n BETWEEN 1 AND $count;
        """.trimIndent())
        log.info("[seed] products={}", query)
    }

    private fun seedLikes(attempts: Int, productCount: Int) {
        // TODO: seed.sql 의 좋아요 INSERT IGNORE...SELECT 를 옮기고, 범위를  WHERE seq < $attempts  로
        val query = jdbcTemplate.update("""
            INSERT IGNORE INTO likes (user_id, product_id, liked_at, created_at, updated_at)
            SELECT
              1 + (seq % 50000)                          AS user_id,
              1 + FLOOR(POW(RAND(), 4) * $productCount)  AS product_id,
              NOW(6), NOW(6), NOW(6)
            FROM (
              SELECT (d6.i*100000 + d5.i*10000 + d4.i*1000 + d3.i*100 + d2.i*10 + d1.i) AS seq
              FROM (SELECT 0 i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d1
              CROSS JOIN (SELECT 0 i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d2
              CROSS JOIN (SELECT 0 i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d3
              CROSS JOIN (SELECT 0 i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d4
              CROSS JOIN (SELECT 0 i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d5
              CROSS JOIN (SELECT 0 i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d6
            ) t
            WHERE seq < $attempts;
        """.trimIndent())
        log.info("[seed] likes(attempts)={}", query)
    }
}
