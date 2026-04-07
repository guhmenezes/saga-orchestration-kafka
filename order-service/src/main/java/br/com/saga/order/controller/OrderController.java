package br.com.saga.order.controller;

import br.com.saga.order.event.OrderCreatedEvent;
import br.com.saga.order.producer.OrderProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderProducer producer;

    public OrderController(OrderProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public String createOrder() {

        String orderId = UUID.randomUUID().toString();

        OrderCreatedEvent event = new OrderCreatedEvent(
                orderId,
                "product-1",
                2
        );

        producer.send(event);

        return "Order sent with ID: " + orderId;
    }
}