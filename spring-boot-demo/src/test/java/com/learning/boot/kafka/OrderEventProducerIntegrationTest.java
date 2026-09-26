package com.learning.boot.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = "orders-test",
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
class OrderEventProducerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Autowired
    private OrderEventProducer producer;

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Test
    void shouldPublishOrderEvent() {
        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        1001L,
                        42L,
                        new BigDecimal("99.95"),
                        Instant.now()
                );

        kafkaTemplate.send(
                "orders-test",
                "1001",
                event
        );

        Map<String, Object> consumerProperties =
                KafkaTestUtils.consumerProps(
                        broker,
                        "orders-test-group",
                        false
                );

        consumerProperties.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        try(
                Consumer<String, OrderCreatedEvent> consumer =
                        createConsumer(consumerProperties)
                ){
            broker.consumeFromAnEmbeddedTopic(
                    consumer,
                    "orders-test"
            );

            ConsumerRecord<String, OrderCreatedEvent>
                    record =
                    KafkaTestUtils.getSingleRecord(
                            consumer,
                    "orders-test"
            );

            assertThat(record.key())
                    .isEqualTo("1001");

            assertThat(record.value().orderId())
                    .isEqualTo(1001L);
            assertThat(record.value().total())
                    .isEqualByComparingTo(
                            new BigDecimal("99.95")
                    );
        }
    }

    private Consumer<
            String,
            OrderCreatedEvent
            > createConsumer(
                    Map<String, Object> properties
    ) {
        properties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        properties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JacksonJsonDeserializer.class
        );

        properties.put(
                JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,
                OrderCreatedEvent.class
        );

        properties.put(
                JacksonJsonDeserializer.TRUSTED_PACKAGES,
                "com.learning.boot.kafka"
        );

        return new DefaultKafkaConsumerFactory<
                String, OrderCreatedEvent>(properties).createConsumer();
    }

}
