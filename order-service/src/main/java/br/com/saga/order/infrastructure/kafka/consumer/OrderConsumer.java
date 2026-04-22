package br.com.saga.order.infrastructure.kafka.consumer;

import br.com.saga.common.event.order.CancelOrderEvent;
import br.com.saga.common.event.order.CreateOrderEvent;
import br.com.saga.common.event.order.OrderCancelledEvent;
import br.com.saga.common.event.order.OrderCreatedEvent;
import br.com.saga.common.event.order.OrderFailedEvent;
import br.com.saga.common.event.payment.PaymentApprovedEvent;
import br.com.saga.order.core.service.OrderService;
import br.com.saga.order.infrastructure.kafka.producer.OrderProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final OrderProducer producer;
    private final OrderService service;

    @KafkaListener(topics = "create-order")
    public void consumeCreateOrder(CreateOrderEvent event) {
        log.info("Recebido comando para criar pedido: {}", event.payload().orderId());
        try {
            service.createOrder(event.payload());
            producer.sendOrderCreated(new OrderCreatedEvent(event.payload()));
        } catch (Exception e) {
            log.error("Erro ao criar pedido no banco: {}", e.getMessage());
            producer.sendOrderFailed(new OrderFailedEvent(event.payload(), e.getMessage()));
        }
    }

    @KafkaListener(topics = "finish-order")
    public void consumeFinishOrder(PaymentApprovedEvent event) {
        log.info("Finalizando pedido no banco: {}", event.payload().orderId());
        service.completeOrder(event.payload().orderId());
    }

    @KafkaListener(topics = "order-cancel")
    public void consumeCancelOrder(CancelOrderEvent event) {
        log.warn("Cancelando pedido no banco por compensação: {}", event.payload().orderId());
        service.cancelOrder(event.payload().orderId());
        producer.sendOrderCancelled(new OrderCancelledEvent(event.payload()));
    }
}