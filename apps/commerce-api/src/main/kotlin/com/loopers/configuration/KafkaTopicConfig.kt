package com.loopers.configuration

import com.loopers.config.kafka.KafkaTopics
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicConfig {
    @Bean
    fun catalogEventsTopic(): NewTopic =
        TopicBuilder.name(KafkaTopics.CATALOG_EVENTS)
            .partitions(3)
            .replicas(1)
            .build()
}
