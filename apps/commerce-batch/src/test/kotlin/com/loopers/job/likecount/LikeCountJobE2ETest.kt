package com.loopers.job.likecount

import com.loopers.batch.job.likecount.LikeCountRefreshJobConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@SpringBatchTest
@TestPropertySource(properties = ["spring.batch.job.name=${LikeCountRefreshJobConfig.JOB_NAME}"])
class LikeCountJobE2ETest @Autowired constructor(
    // IDE 정적 분석 상 [SpringBatchTest] 의 주입보다 [SpringBootTest] 의 주입이 우선되어, 해당 컴포넌트는 없으므로 오류처럼 보일 수 있음.
    // [SpringBatchTest] 자체가 Scope 기반으로 주입하기 때문에 정상 동작함.
    private val jobLauncherTestUtils: JobLauncherTestUtils,
    @param:Qualifier(LikeCountRefreshJobConfig.JOB_NAME) private val job: Job,
    private val jdbcTemplate: JdbcTemplate,
) {
    @BeforeEach
    fun beforeEach() {
        dropAll()

        jdbcTemplate.execute("CREATE TABLE products (id BIGINT PRIMARY KEY)")
        jdbcTemplate.execute("CREATE TABLE likes (id BIGINT AUTO_INCREMENT PRIMARY KEY, product_id BIGINT, deleted_at DATETIME(6) NULL)")
        jdbcTemplate.execute("CREATE TABLE product_like_count (product_id BIGINT PRIMARY KEY, like_count INT NOT NULL, refreshed_at DATETIME(6) NOT NULL, INDEX idx_product_like_count (like_count))")

        jdbcTemplate.update("INSERT INTO products (id) VALUES (1), (2), (3)")
        jdbcTemplate.update("INSERT INTO likes (product_id, deleted_at) VALUES (1, NULL), (1, NULL)")
        jdbcTemplate.update("INSERT INTO likes (product_id, deleted_at) VALUES (2, NULL), (2, NOW(6))")
    }

    @AfterEach
    fun tearDown() = dropAll()

    private fun dropAll() {
        jdbcTemplate.execute(
            "DROP TABLE IF EXISTS products, likes, product_like_count, product_like_count_new, product_like_count_old",
        )
    }

    @DisplayName("좋아요 수 집계 MV가 정확히 REFRESH 된다 (soft-delete 제외, 0개 포함, swap 정리)")
    @Test
    fun refresh_aggregatesLikeCount() {
        // arrange
        jobLauncherTestUtils.job = job

        // act
        val jobExecution = jobLauncherTestUtils.launchJob()

        // assert
        assertAll(
            { assertThat(jobExecution.exitStatus.exitCode).isEqualTo(ExitStatus.COMPLETED.exitCode) },
            { assertThat(likeCountOf(1)).isEqualTo(2) },
            { assertThat(likeCountOf(2)).isEqualTo(1) },
            { assertThat(likeCountOf(3)).isEqualTo(0) },
            { assertThat(tableExists("product_like_count_new")).isFalse() },
            { assertThat(tableExists("product_like_count_old")).isFalse() },
        )
    }

    private fun likeCountOf(productId: Long): Int =
        jdbcTemplate.queryForObject(
            "SELECT like_count FROM product_like_count WHERE product_id = ?",
            Int::class.java,
            productId,
        )!!

    private fun tableExists(name: String): Boolean =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
            Int::class.java,
            name,
        )!! > 0
}
