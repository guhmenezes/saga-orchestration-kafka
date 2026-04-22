package br.com.saga.inventory.infrastructure.kafka.consumer;

import br.com.saga.common.event.inventory.CompensateStockEvent;
import br.com.saga.common.event.inventory.ReserveStockEvent;
import br.com.saga.common.event.inventory.StockCompensatedEvent;
import br.com.saga.common.event.inventory.StockFailedEvent;
import br.com.saga.common.event.inventory.StockReservedEvent;
import br.com.saga.inventory.core.service.InventoryService;
import br.com.saga.inventory.infrastructure.kafka.producer.InventoryProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryConsumer {

    private final InventoryProducer producer;
    private final InventoryService service;

    @KafkaListener(topics = "reserve-stock")
    public void consumeReserve(ReserveStockEvent event) {
        log.info("Recebido comando de reserva para o pedido: {}", event.payload().orderId());
        try {
            service.updateStock(event.payload());
            producer.sendStockReserved(new StockReservedEvent(event.payload()));
        } catch (Exception e) {
            log.error("Erro ao reservar estoque: {}", e.getMessage());
            producer.sendStockFailed(new StockFailedEvent(event.payload(), e.getMessage()));
        }
    }

    @KafkaListener(topics = "compensate-stock")
    public void consumeCompensate(CompensateStockEvent event) {
        log.warn("Recebido comando de compensação para o pedido: {}", event.payload().orderId());
        try {
            service.rollbackStock(event.payload());
            producer.sendStockCompensated(new StockCompensatedEvent(event.payload()));
        } catch (Exception e) {
            log.error("Erro crítico ao compensar estoque: {}", e.getMessage());
        }
    }
}