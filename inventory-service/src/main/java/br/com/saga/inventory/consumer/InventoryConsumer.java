package br.com.saga.inventory.consumer;

import br.com.saga.inventory.event.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryConsumer {

    @KafkaListener(topics = "order-created", groupId = "inventory-group")
    public void consume(OrderCreatedEvent event) {

        System.out.println("📦 Processing order: " + event.orderId());

        // simula processamento
        System.out.println("✅ Stock reserved for product: " + event.product());
    }
}