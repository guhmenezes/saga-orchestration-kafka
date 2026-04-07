package br.com.saga.orchestrator.consumer;

import br.com.saga.orchestrator.event.StockReservedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrchestratorConsumer {

    @KafkaListener(topics = "stock-reserved", groupId = "orchestrator-group")
    public void consume(StockReservedEvent event) {

        System.out.println("🧠 Orchestrator received stock confirmation for order: " + event.orderId());

    }
}