package br.com.saga.orchestrator.infrastructure.kafka.producer;

import br.com.saga.common.event.order.CancelOrderEvent;
import br.com.saga.common.event.inventory.CompensateStockEvent;
import br.com.saga.common.event.order.CreateOrderEvent;
import br.com.saga.common.event.payment.PaymentRequestEvent;
import br.com.saga.common.event.inventory.ReserveStockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrchestratorProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCreateOrder(CreateOrderEvent event) {
        log.info("SAGA [STEP 1] -> Enviando comando de criação de pedido: {}", event.payload().orderId());
        kafkaTemplate.send("create-order", event.payload().orderId(), event);
    }

    public void sendReserveStock(ReserveStockEvent event) {
        log.info("SAGA [STEP 2] -> Enviando comando de reserva de estoque: {}", event.payload().orderId());
        kafkaTemplate.send("reserve-stock", event.payload().orderId(), event);
    }

    public void sendProcessPayment(PaymentRequestEvent event) {
        log.info("SAGA [STEP 3] -> Enviando comando de processamento de pagamento: {}", event.payload().orderId());
        kafkaTemplate.send("process-payment", event.payload().orderId(), event);
    }

    public void sendCancelOrder(CancelOrderEvent event) {
        log.warn("SAGA [ROLLBACK] -> Enviando comando de cancelamento final para Order Service: {}", event.payload().orderId());
        kafkaTemplate.send("order-cancel", event.payload().orderId(),event);
    }

    public void sendCompensateStock(CompensateStockEvent event) {
        log.warn("SAGA [ROLLBACK] -> Enviando comando de estorno de estoque: {}", event.payload().orderId());
        kafkaTemplate.send("compensate-stock", event.payload().orderId(), event);
    }
}