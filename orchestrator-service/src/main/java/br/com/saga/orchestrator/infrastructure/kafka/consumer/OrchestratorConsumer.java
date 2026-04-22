package br.com.saga.orchestrator.infrastructure.kafka.consumer;

import br.com.saga.common.event.order.OrderCancelledEvent;
import br.com.saga.common.event.order.OrderCreatedEvent;
import br.com.saga.common.event.order.OrderFailedEvent;
import br.com.saga.common.event.payment.PaymentApprovedEvent;
import br.com.saga.common.event.payment.PaymentFailedEvent;
import br.com.saga.common.event.payment.PaymentRefundedEvent;
import br.com.saga.common.event.inventory.StockCompensatedEvent;
import br.com.saga.common.event.inventory.StockFailedEvent;
import br.com.saga.common.event.inventory.StockReservedEvent;
import br.com.saga.orchestrator.core.service.SagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrchestratorConsumer {

    private final SagaService service;

    @KafkaListener(topics = "order-created")
    public void orderCreated(OrderCreatedEvent event) {
        service.handleOrderCreated(event);
    }

    @KafkaListener(topics = "order-failed")
    public void orderCreated(OrderFailedEvent event) {
        service.handleOrderFail(event);
    }

    @KafkaListener(topics = "order-cancelled")
    public void orderCancelled(OrderCancelledEvent event) {
        service.handleOrderCancelled(event);
    }

    @KafkaListener(topics = "stock-reserved")
    public void stockReserved(StockReservedEvent event) {
        service.handleStockReserved(event);
    }

    @KafkaListener(topics = "stock-failed")
    public void stockFailed(StockFailedEvent event) {
        service.handleStockFail(event);
    }

    @KafkaListener(topics = "stock-compensated")
    public void stockCompensated(StockCompensatedEvent event) {
        service.handleStockCompensated(event);
    }

    @KafkaListener(topics = "payment-approved")
    public void paymentApproved(PaymentApprovedEvent event) {
        service.handlePaymentApproved(event);
    }

    @KafkaListener(topics = "payment-failed")
    public void paymentFailed(PaymentFailedEvent event) {
        service.handlePaymentFail(event);
    }

    @KafkaListener(topics = "payment-refunded")
    public void paymentRefunded(PaymentRefundedEvent event) {
        service.handlePaymentRefunded(event);
    }
}