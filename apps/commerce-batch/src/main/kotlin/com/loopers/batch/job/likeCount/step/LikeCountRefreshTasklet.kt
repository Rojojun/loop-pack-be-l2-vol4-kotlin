package com.loopers.batch.job.likecount.step

import com.loopers.batch.job.likecount.LikeCountRefreshJobConfig
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@StepScope
@ConditionalOnProperty(name = ["spring.batch.job.name"], havingValue = LikeCountRefreshJobConfig.JOB_NAME)
@Component
class LikeCountRefreshTasklet(
    private val jdbcTemplate: JdbcTemplate,
) : Tasklet {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        val start = System.currentTimeMillis()
        log.info("[LikeCount] Refresh 시작")
        jdbcTemplate.execute("DROP TABLE IF EXISTS product_like_count_new")
        jdbcTemplate.execute("CREATE TABLE product_like_count_new LIKE product_like_count")

        val affected = jdbcTemplate.update("""
            INSERT INTO product_like_count_new (product_id, like_count, refreshed_at)
            SELECT p.id, COUNT(l.id), NOW(6)
            FROM products p
            LEFT JOIN likes l ON l.product_id = p.id AND l.deleted_at IS NULL
            GROUP BY p.id
        """.trimIndent())

        jdbcTemplate.execute("RENAME TABLE product_like_count TO product_like_count_old, product_like_count_new TO product_like_count")
        jdbcTemplate.execute("DROP TABLE product_like_count_old")
        log.info("[LikeCount] Refresh 완료 --- rows={}, spend_time={}ms", affected, System.currentTimeMillis() - start)
        return RepeatStatus.FINISHED
    }
}
