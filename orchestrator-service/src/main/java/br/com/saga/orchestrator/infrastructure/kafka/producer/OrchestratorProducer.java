package br.com.saga.orchestrator.infrastructure.kafka.producer;

import br.com.saga.common.event.CreateOrderEvent;
import br.com.saga.common.event.PaymentRequestEvent;
import br.com.saga.common.event.ReserveStockEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrchestratorProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCreateOrder(CreateOrderEvent event) {
        kafkaTemplate.send("create-order", event);
    }

    public void sendReserveStock(ReserveStockEvent event) {
        kafkaTemplate.send("reserve-stock", event);
    }

    public void sendProcessPayment(PaymentRequestEvent event) {
        kafkaTemplate.send("process-payment", event);
    }
}