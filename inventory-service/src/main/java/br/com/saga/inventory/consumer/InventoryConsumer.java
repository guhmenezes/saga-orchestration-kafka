package br.com.saga.inventory.consumer;

import br.com.saga.common.event.OrderCreatedEvent;
import br.com.saga.common.event.StockReservedEvent;
import br.com.saga.inventory.producer.InventoryProducer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryConsumer {

    private final InventoryProducer producer;

    public InventoryConsumer(InventoryProducer producer) {
        this.producer = producer;
    }

    @KafkaListener(topics = "order-created", groupId = "inventory-group")
    public void consume(OrderCreatedEvent event) {

        System.out.println("📦 Processing order: " + event.orderId());

        StockReservedEvent response = new StockReservedEvent(
                event.orderId(),
                event.productId(),
                event.quantity()
        );

        producer.send(response);

        System.out.println("✅ Stock reserved and event published!");
    }
}