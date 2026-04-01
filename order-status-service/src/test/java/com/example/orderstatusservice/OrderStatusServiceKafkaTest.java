package com.example.orderstatusservice;

import com.example.model.OrderEvent;
import com.example.model.OrderStatusEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class OrderStatusServiceKafkaTest {

    @Container
    static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @DynamicPropertySource
    static void configureKafka(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Test
    void testOrderEventProducesStatusEvent() throws ExecutionException, InterruptedException {
        // Producer for order-topic
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        DefaultKafkaProducerFactory<String, OrderEvent> producerFactory = new DefaultKafkaProducerFactory<>(producerProps);
        KafkaTemplate<String, OrderEvent> kafkaTemplate = new KafkaTemplate<>(producerFactory);

        // Consumer for order-status-topic
        Map<String, Object> consumerProp = new HashMap<>();
        consumerProp.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        consumerProp.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProp.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProp.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProp.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProp.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        DefaultKafkaConsumerFactory<String, OrderStatusEvent> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProp);

        ContainerProperties containerProps = new ContainerProperties("order-status-topic");
        BlockingQueue<OrderStatusEvent> queue = new ArrayBlockingQueue<>(10);

        containerProps.setMessageListener((MessageListener<String, OrderStatusEvent>) record -> queue.add(record.value()));

        KafkaMessageListenerContainer<String, OrderStatusEvent> container = new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
        container.start();

        try {
            OrderEvent event = new OrderEvent("phone", 2);
            kafkaTemplate.send("order-topic", "phone-key", event).get();

            OrderStatusEvent statusEvent = queue.poll(10, TimeUnit.SECONDS);

            assertThat(statusEvent).isNotNull();
            assertThat(statusEvent.getStatus()).isEqualTo("CREATED");
            assertThat(statusEvent.getDate()).isNotNull();
        } finally {
            container.stop();
        }
    }
}
