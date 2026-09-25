package com.learning.boot.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
           KafkaTemplate<Object, Object> kafkaTemplate
    ) {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(kafkaTemplate);
        FixedBackOff backOff =
                new FixedBackOff(
                        1_000L,
                        2L
                );

        return new DefaultErrorHandler(
//                (record, exception) -> {
//                    System.err.println(
//                            "Record failed : "  +
//                                    record.topic() +
//                                    " - " +
//                                    record.partition() +
//                                    " @ " +
//                                    record.offset()
//                    );
//                },
                    recoverer,
                backOff
        );
    }
}
