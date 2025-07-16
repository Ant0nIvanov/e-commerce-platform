package ru.ivanov.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic createUserCreatedEventTopic() {
        return TopicBuilder
                .name("user-created-event-topic")
                .partitions(3)
                .replicas(3)
                .config("min.insync.replicas", "2")
                .build();
    }

    @Bean NewTopic createUserDeletedEventTopic() {
        return TopicBuilder
                .name("user-deleted-event-topic")
                .partitions(3)
                .replicas(3)
                .config("min.insync.replicas", "2")
                .build();
    }
}