package br.com.saga.orchestrator.producer;

import br.com.saga.orchestrator.event.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrchestratorProducer {

    private static final String TOPIC = "order-created";

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrchestratorProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(OrderCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.orderId(), event);
    }
}