package br.com.saga.order.infrastructure.kafka.producer;

import br.com.saga.common.event.order.OrderCancelledEvent;
import br.com.saga.common.event.order.OrderCreatedEvent;
import br.com.saga.common.event.order.OrderFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreated(OrderCreatedEvent event) {
        log.info("Enviando confirmação de pedido criado: {}", event.payload().orderId());
        kafkaTemplate.send("order-created", event.payload().orderId(), event);
    }

    public void sendOrderFailed(OrderFailedEvent event) {
        log.error("Enviando aviso de falha na criação do pedido: {}", event.payload().orderId());
        kafkaTemplate.send("order-failed", event.payload().orderId(), event);
    }

    public void sendOrderCancelled(OrderCancelledEvent event) {
        log.info("Enviando confirmação de cancelamento de pedido: {}", event.payload().orderId());
        kafkaTemplate.send("order-cancelled", event.payload().orderId(), event);
    }
}
