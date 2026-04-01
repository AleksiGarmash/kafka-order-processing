package com.example.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.order-topic}")
    private String orderTopic;

    @Value("${app.kafka.order-status-topic}")
    private String orderStatusTopic;

    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(orderTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic orderStatusTopic() {
        return TopicBuilder.name(orderStatusTopic).partitions(1).replicas(1).build();
    }
}
