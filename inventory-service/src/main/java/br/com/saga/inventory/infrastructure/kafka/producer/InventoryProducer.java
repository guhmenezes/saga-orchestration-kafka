package br.com.saga.inventory.infrastructure.kafka.producer;

import br.com.saga.common.event.inventory.StockCompensatedEvent;
import br.com.saga.common.event.inventory.StockFailedEvent;
import br.com.saga.common.event.inventory.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendStockReserved(StockReservedEvent event) {
        log.info("Enviando confirmação de estoque: {}", event.payload().orderId());
        kafkaTemplate.send("stock-reserved", event.payload().orderId(), event);
    }

    public void sendStockFailed(StockFailedEvent event) {
        log.warn("Enviando falha de estoque: {}", event.payload().orderId());
        kafkaTemplate.send("stock-failed", event.payload().orderId(), event);
    }

    public void sendStockCompensated(StockCompensatedEvent event) {
        log.info("Enviando confirmação de estorno de estoque: {}", event.payload().orderId());
        kafkaTemplate.send("stock-compensated", event.payload().orderId(), event);
    }
}
