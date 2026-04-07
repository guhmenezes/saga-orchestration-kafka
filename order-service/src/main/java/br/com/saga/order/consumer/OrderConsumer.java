package br.com.saga.order.consumer;

import br.com.saga.order.event.StockReservedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    @KafkaListener(topics = "stock-reserved", groupId = "order-group")
    public void consume(StockReservedEvent event) {

        System.out.println("🎯 Order completed: " + event.orderId());
    }
}