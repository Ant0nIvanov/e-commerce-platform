package ru.ivanov.cartservice.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.MessageListener;
import ru.ivanov.common.event.UserCreatedEvent;

@Slf4j
public class KafkaConfig {

//    @Bean
//    public MessageListener<String, UserCreatedEvent> messageListener() {
//        return (record) -> {
//            log.info("Received message from {}--{}--{}",
//                    record.topic(),
//                    record.partition(),
//                    record.offset()
//            );
//
//
//        }
//    }
}
