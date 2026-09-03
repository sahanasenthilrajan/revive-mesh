package com.revivemesh.backend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String PAYMENT_FAILURES_TOPIC = "payment-failures";
    public static final String FAILURE_CLUSTERS_TOPIC = "failure-clusters";

    @Bean
    public NewTopic paymentFailuresTopic() {
        return TopicBuilder.name(PAYMENT_FAILURES_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic failureClustersTopic() {
        return TopicBuilder.name(FAILURE_CLUSTERS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
