package br.com.saga.orchestrator.core.service;

import br.com.saga.common.event.CreateOrderEvent;
import br.com.saga.common.event.OrderCreatedEvent;
import br.com.saga.common.event.PaymentRequestEvent;
import br.com.saga.common.event.PaymentApprovedEvent;
import br.com.saga.common.event.ReserveStockEvent;
import br.com.saga.common.event.StockReservedEvent;
import br.com.saga.orchestrator.api.dto.CreateOrderRequestDTO;
import br.com.saga.orchestrator.core.domain.Saga;
import br.com.saga.orchestrator.core.domain.SagaStatus;
import br.com.saga.orchestrator.core.repository.SagaRepository;
import br.com.saga.orchestrator.infrastructure.kafka.producer.OrchestratorProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SagaService {

    private final OrchestratorProducer producer;
    private final SagaRepository repository;

    public String startSaga(CreateOrderRequestDTO request) {
        String orderId = UUID.randomUUID().toString();

        Saga saga = Saga.builder()
                .orderId(orderId)
                .status(SagaStatus.STARTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        saga.addHistory(SagaStatus.STARTED.name(), "Iniciando processo de compra para o produto: " + request.productId());
        repository.save(saga);

        System.out.println("Starting saga: " + orderId);
        producer.sendCreateOrder(new CreateOrderEvent(orderId, request.productId(), request.quantity()));

        return orderId;
    }

    public void handleOrderCreated(OrderCreatedEvent event) {
        updateSagaStatus(event.orderId(), SagaStatus.ORDER_SUCCESS, "Pedido criado com sucesso");

        System.out.println("Order created: " + event.orderId());
        producer.sendReserveStock(new ReserveStockEvent(event.orderId(), event.productId(), event.quantity()));
    }

    public void handleStockReserved(StockReservedEvent event) {
        updateSagaStatus(event.orderId(), SagaStatus.INVENTORY_SUCCESS, "Estoque reservado com sucesso");

        System.out.println("Stock reserved: " + event.orderId());
        producer.sendProcessPayment(new PaymentRequestEvent(event.orderId(), event.productId(), event.quantity()));
    }

    public void handlePaymentApproved(PaymentApprovedEvent event) {
        System.out.println("Payment approved: " + event.orderId());

        completeSaga(event.orderId(), SagaStatus.SAGA_FINISHED, "Pagamento aprovado. Saga concluída com sucesso.");
        System.out.println("SAGA FINALIZADA COM SUCESSO");
    }

    private void updateSagaStatus(String orderId, SagaStatus status, String details) {
        Saga saga = repository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Saga não encontrada para o ID: " + orderId));

        saga.setStatus(status);
        saga.setUpdatedAt(LocalDateTime.now());
        saga.addHistory(status.name(), details);
        repository.save(saga);
    }

    private void completeSaga(String orderId, SagaStatus status, String details) {
        Saga saga = repository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Saga não encontrada para o ID: " + orderId));

        saga.setStatus(status);
        saga.setUpdatedAt(LocalDateTime.now());
        saga.setFinishedAt(LocalDateTime.now());
        saga.addHistory(status.name(), details);

        repository.save(saga);
    }
}