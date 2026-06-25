package com.loopers.batch.job.likecount

import com.loopers.batch.job.likecount.step.LikeCountRefreshTasklet
import com.loopers.batch.listener.JobListener
import com.loopers.batch.listener.StepMonitorListener
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.support.transaction.ResourcelessTransactionManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConditionalOnProperty(name = ["spring.batch.job.name"], havingValue = LikeCountRefreshJobConfig.JOB_NAME)
@Configuration
class LikeCountRefreshJobConfig(
    private val jobRepository: JobRepository,
    private val jobListener: JobListener,
    private val stepMonitorListener: StepMonitorListener,
    private val likeCountRefreshTasklet: LikeCountRefreshTasklet
) {
    @Bean(JOB_NAME)
    fun likeCountJob(): Job =
        JobBuilder(JOB_NAME, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(likeCountRefreshStep())
            .listener(jobListener)
            .build()

    @JobScope
    @Bean(STEP_LIKE_COUNT_SIMPLE_TASK_NAME)
    fun likeCountRefreshStep(): Step =
        StepBuilder(STEP_LIKE_COUNT_SIMPLE_TASK_NAME, jobRepository)
            .tasklet(likeCountRefreshTasklet, ResourcelessTransactionManager())
            .listener(stepMonitorListener)
            .build()


    companion object {
        const val JOB_NAME = "likeCountRefreshJob"
        private const val STEP_LIKE_COUNT_SIMPLE_TASK_NAME = "likeCountSimpleTask"
    }
}
