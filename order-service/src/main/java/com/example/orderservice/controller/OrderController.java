package com.example.orderservice.controller;

import com.example.model.Order;
import com.example.model.OrderEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final String orderTopic;

    public OrderController(KafkaTemplate<String, OrderEvent> kafkaTemplate,
                           @Value("${app.kafka.order-topic}") String orderTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderTopic = orderTopic;
    }

    @PostMapping
    public String createOrder(@RequestBody Order order) {
        OrderEvent event = new OrderEvent(order.getProduct(), order.getQuantity());
        kafkaTemplate.send(orderTopic, event.getProduct(), event);
        return "Order sent to Kafka: " + event;
    }
}
