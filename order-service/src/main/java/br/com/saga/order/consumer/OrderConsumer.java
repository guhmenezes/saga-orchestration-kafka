package br.com.saga.order.consumer;

import br.com.saga.common.event.CreateOrderEvent;
import br.com.saga.common.event.OrderCreatedEvent;
import br.com.saga.order.producer.OrderProducer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    private final OrderProducer producer;

    public OrderConsumer(OrderProducer producer) {
        this.producer = producer;
    }

    @KafkaListener(topics = "create-order")
    public void consume(CreateOrderEvent event) {

        System.out.println("📝 Creating order: " + event.orderId());

        producer.send(new OrderCreatedEvent(event.orderId(), event.productId(), event.quantity()));
    }
}