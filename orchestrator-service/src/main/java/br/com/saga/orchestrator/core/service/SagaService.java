package br.com.saga.orchestrator.core.service;

import br.com.saga.common.event.inventory.CompensateStockEvent;
import br.com.saga.common.event.inventory.ReserveStockEvent;
import br.com.saga.common.event.inventory.StockCompensatedEvent;
import br.com.saga.common.event.inventory.StockFailedEvent;
import br.com.saga.common.event.inventory.StockReservedEvent;
import br.com.saga.common.event.order.CancelOrderEvent;
import br.com.saga.common.event.order.CreateOrderEvent;
import br.com.saga.common.event.order.OrderCancelledEvent;
import br.com.saga.common.event.order.OrderCreatedEvent;
import br.com.saga.common.event.order.OrderFailedEvent;
import br.com.saga.common.event.payment.PaymentApprovedEvent;
import br.com.saga.common.event.payment.PaymentFailedEvent;
import br.com.saga.common.event.payment.PaymentRefundedEvent;
import br.com.saga.common.event.payment.PaymentRequestEvent;
import br.com.saga.common.payload.OrderPayload;
import br.com.saga.orchestrator.api.dto.CreateOrderRequestDTO;
import br.com.saga.orchestrator.core.domain.Saga;
import br.com.saga.orchestrator.core.domain.SagaStatus;
import br.com.saga.orchestrator.core.repository.SagaRepository;
import br.com.saga.orchestrator.infrastructure.kafka.producer.OrchestratorProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaService {

    private final OrchestratorProducer producer;
    private final SagaRepository repository;

    public String startSaga(CreateOrderRequestDTO request) {
        String orderId = UUID.randomUUID().toString();
        OrderPayload payload = new OrderPayload(orderId, request.productId(), request.quantity());

        Saga saga = Saga.builder()
                .orderId(orderId)
                .status(SagaStatus.SAGA_STARTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        saga.addHistory(SagaStatus.SAGA_STARTED.name(), "Iniciando compra do produto: " + payload.productId());
        repository.save(saga);

        log.info("Starting saga: {}", orderId);
        producer.sendCreateOrder(new CreateOrderEvent(payload));

        return orderId;
    }

    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Order created: {}", event.payload().orderId());
        updateSagaStatus(event.payload().orderId(), SagaStatus.ORDER_SUCCESS, "Pedido criado com sucesso");

        producer.sendReserveStock(new ReserveStockEvent(event.payload()));
    }

    public void handleOrderFail(OrderFailedEvent event) {
        log.error("Order creation failed: {} | Reason: {}", event.payload().orderId(), event.reason());
        updateSagaStatus(event.payload().orderId(), SagaStatus.ORDER_FAIL, "Falha no pedido: " + event.reason());
        completeSaga(event.payload().orderId(), SagaStatus.SAGA_FAILED, "Saga falhou");
    }

    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("Confirmação de cancelamento de pedido recebida: {}", event.payload().orderId());
        completeSaga(event.payload().orderId(), SagaStatus.SAGA_COMPENSATED, "Saga totalmente compensada");
    }

    public void handleStockReserved(StockReservedEvent event) {
        log.info("Stock reserved: {}", event.payload().orderId());
        updateSagaStatus(event.payload().orderId(), SagaStatus.INVENTORY_SUCCESS, "Estoque reservado com sucesso");

        producer.sendProcessPayment(new PaymentRequestEvent(event.payload()));
    }

    public void handleStockFail(StockFailedEvent event) {
        log.warn("Stock failed for order: {} | Reason: {}", event.payload().orderId(), event.reason());
        updateSagaStatus(event.payload().orderId(), SagaStatus.INVENTORY_FAIL, event.reason());
        updateSagaStatus(event.payload().orderId(), SagaStatus.COMPENSATING, "Solicitando cancelamento de pedido");

        CancelOrderEvent cancelOrderEvent = new CancelOrderEvent(event.payload());
        producer.sendCancelOrder(cancelOrderEvent);
    }

    public void handleStockCompensated(StockCompensatedEvent event) {
        log.info("Stock compensation finished for order: {}", event.payload().orderId());
        updateSagaStatus(event.payload().orderId(), SagaStatus.COMPENSATING, "Solicitando cancelamento de pedido");

        CancelOrderEvent cancelOrderEvent = new CancelOrderEvent(event.payload());
        producer.sendCancelOrder(cancelOrderEvent);
    }

    public void handlePaymentApproved(PaymentApprovedEvent event) {
        log.info("Payment approved: {}", event.payload().orderId());
        updateSagaStatus(event.payload().orderId(), SagaStatus.PAYMENT_SUCCESS, "Pagamento aprovado");
        completeSaga(event.payload().orderId(), SagaStatus.SAGA_FINISHED, "Saga finalizada com sucesso");
    }

    public void handlePaymentFail(PaymentFailedEvent event) {
        log.warn("Payment failed for order: {} | Reason: {}", event.payload().orderId(), event.reason());

        updateSagaStatus(event.payload().orderId(), SagaStatus.PAYMENT_FAIL, event.reason());
        updateSagaStatus(event.payload().orderId(), SagaStatus.COMPENSATING, "Solicitando estorno de estoque");

        producer.sendCompensateStock(new CompensateStockEvent(event.payload()));
    }

    public void handlePaymentRefunded(PaymentRefundedEvent event) {
        // TODO: Implementar processamento de estorno financeiro no MS Payment.
        // Para esta versão inicial (MVP), a Saga foca apenas na compensação crítica de estoque e pedido.
        log.info("Confirmação de estorno de pagamento recebida: {}", event.payload().orderId());
    }

    private void updateSagaStatus(String orderId, SagaStatus status, String details) {
        Saga saga = findSagaOrThrow(orderId);
        saga.setStatus(status);
        saga.setUpdatedAt(LocalDateTime.now());
        saga.addHistory(status.name(), details);
        repository.save(saga);
    }

    private void completeSaga(String orderId, SagaStatus status, String details) {
        Saga saga = findSagaOrThrow(orderId);
        saga.setStatus(status);
        saga.setUpdatedAt(LocalDateTime.now());
        saga.setFinishedAt(LocalDateTime.now());
        saga.addHistory(status.name(), details);
        repository.save(saga);
    }

    private Saga findSagaOrThrow(String orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Saga não encontrada para o ID: " + orderId));
    }
}