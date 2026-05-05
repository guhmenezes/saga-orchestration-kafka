package br.com.saga.inventory.infrastructure.kafka.consumer;

import br.com.saga.common.event.inventory.CompensateStockEvent;
import br.com.saga.common.event.inventory.ReserveStockEvent;
import br.com.saga.common.event.inventory.StockCompensatedEvent;
import br.com.saga.common.event.inventory.StockFailedEvent;
import br.com.saga.common.event.inventory.StockReservedEvent;
import br.com.saga.inventory.core.exceptions.InsufficientStockException;
import br.com.saga.inventory.core.exceptions.StockNotFoundException;
import br.com.saga.inventory.core.service.InventoryService;
import br.com.saga.inventory.infrastructure.kafka.producer.InventoryProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryConsumer {

    private final InventoryProducer producer;
    private final InventoryService service;

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            exclude = {InsufficientStockException.class, StockNotFoundException.class},
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(topics = "reserve-stock")
    public void consumeReserve(ReserveStockEvent event) {
        log.info("Recebido comando de reserva para o pedido: {}", event.payload().orderId());
        service.updateStock(event.payload());
        producer.sendStockReserved(new StockReservedEvent(event.payload()));
    }

    @DltHandler
    public void handleReserveDlt(ReserveStockEvent event) {
        log.error("FALHA DEFINITIVA no estoque para o pedido: {}", event.payload().orderId());
        producer.sendStockFailed(new StockFailedEvent(event.payload(), "Erro técnico após retries"));
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = "compensate-stock")
    public void consumeCompensate(CompensateStockEvent event) {
        log.warn("Recebido comando de compensação para o pedido: {}", event.payload().orderId());
        service.rollbackStock(event.payload());
        producer.sendStockCompensated(new StockCompensatedEvent(event.payload()));
    }

    @DltHandler
    public void handleRefundDlt(CompensateStockEvent event) {
        log.error("FALHA DEFINITIVA NO ESTORNO DE ESTOQUE");
        producer.sendStockFailed(new StockFailedEvent(event.payload(), "COMPENSATE_FAILED"));
    }
}