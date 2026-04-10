package br.com.saga.orchestrator.consumer;

import br.com.saga.common.event.OrderCreatedEvent;
import br.com.saga.common.event.PaymentApprovedEvent;
import br.com.saga.common.event.StockReservedEvent;
import br.com.saga.orchestrator.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrchestratorConsumer {

    private final OrchestratorService service;

    @KafkaListener(topics = "order-created")
    public void orderCreated(OrderCreatedEvent event) {
        service.handleOrderCreated(event);
    }

    @KafkaListener(topics = "stock-reserved")
    public void stockReserved(StockReservedEvent event) {
        service.handleStockReserved(event);
    }

    @KafkaListener(topics = "payment-approved")
    public void paymentApproved(PaymentApprovedEvent event) {
        service.handlePaymentApproved(event);
    }
}