package br.com.saga.orchestrator.service;

import br.com.saga.common.event.CreateOrderEvent;
import br.com.saga.common.event.OrderCreatedEvent;
import br.com.saga.common.event.PaymentRequestEvent;
import br.com.saga.common.event.PaymentApprovedEvent;
import br.com.saga.common.event.ReserveStockEvent;
import br.com.saga.common.event.StockReservedEvent;
import br.com.saga.orchestrator.dto.CreateOrderRequestDTO;
import br.com.saga.orchestrator.producer.OrchestratorProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

    private final OrchestratorProducer producer;

    public String startSaga(CreateOrderRequestDTO request) {

        String orderId = UUID.randomUUID().toString();

        System.out.println("🚀 Starting saga: " + orderId);

        producer.sendCreateOrder(new CreateOrderEvent(orderId, request.productId(), request.quantity()));

        return orderId;
    }

    public void handleOrderCreated(OrderCreatedEvent event) {

        System.out.println("📦 Order created: " + event.orderId());

        producer.sendReserveStock(
                new ReserveStockEvent(
                        event.orderId(),
                        event.productId(),
                        event.quantity()
                )
        );
    }

    public void handleStockReserved(StockReservedEvent event) {

        System.out.println("📦 Stock reserved: " + event.orderId());

        producer.sendProcessPayment(
                new PaymentRequestEvent(event.orderId(), event.productId(), event.quantity())
        );
    }

    public void handlePaymentApproved(PaymentApprovedEvent event) {

        System.out.println("💰 Payment approved: " + event.orderId());

        System.out.println("✅ SAGA FINALIZADA COM SUCESSO");
    }
}