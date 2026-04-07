package br.com.saga.inventory.producer;

import br.com.saga.inventory.event.StockReservedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryProducer {

    private static final String TOPIC = "stock-reserved";

    private final KafkaTemplate<String, StockReservedEvent> kafkaTemplate;

    public InventoryProducer(KafkaTemplate<String, StockReservedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(StockReservedEvent event) {
        kafkaTemplate.send(TOPIC, event.orderId(), event);
    }
}