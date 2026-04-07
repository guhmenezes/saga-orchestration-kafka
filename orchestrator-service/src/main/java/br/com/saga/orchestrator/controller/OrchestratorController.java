package br.com.saga.orchestrator.controller;

import br.com.saga.orchestrator.event.OrderCreatedEvent;
import br.com.saga.orchestrator.producer.OrchestratorProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrchestratorController {

    private final OrchestratorProducer producer;

    public OrchestratorController(OrchestratorProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public String createOrder() {

        String orderId = UUID.randomUUID().toString();

        OrderCreatedEvent event = new OrderCreatedEvent(
                orderId,
                "product-1",
                1
        );

        producer.send(event);

        return "Saga started for order: " + orderId;
    }
}