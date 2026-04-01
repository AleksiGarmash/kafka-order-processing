package com.example.orderstatusservice.processor;

import com.example.model.OrderEvent;
import com.example.model.OrderStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import java.time.Instant;

@Configuration
public class OrderStatusProcessor {

    private static final Logger log = LoggerFactory.getLogger(OrderStatusProcessor.class);

    private final KafkaTemplate<String, OrderStatusEvent> kafkaTemplate;
    private final String orderStatusTopic;

    public OrderStatusProcessor(KafkaTemplate<String, OrderStatusEvent> kafkaTemplate,
                                @Value("${app.kafka.order-status-topic}") String orderStatusTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderStatusTopic = orderStatusTopic;
    }

    @KafkaListener(
            topics = "${app.kafka.order-topic}",
            groupId = "order-status-service-group"
    )
    public void onOrderReceived(
            OrderEvent orderEvent,
            @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        log.info("Received OrderEvent: {}", orderEvent);

        OrderStatusEvent statusEvent = new OrderStatusEvent("CREATED", Instant.now().toString());
        kafkaTemplate.send(orderStatusTopic, key, statusEvent);

        log.info("Send OrderStatusEvent: {}", statusEvent);
    }
}
