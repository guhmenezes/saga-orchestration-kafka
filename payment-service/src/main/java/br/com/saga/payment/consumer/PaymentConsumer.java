package br.com.saga.payment.consumer;

import br.com.saga.common.event.PaymentApprovedEvent;
import br.com.saga.common.event.PaymentRequestEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {

    private final KafkaTemplate<String, PaymentApprovedEvent> kafkaTemplate;

    public PaymentConsumer(KafkaTemplate<String, PaymentApprovedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "process-payment", groupId = "payment-group")
    public void consume(PaymentRequestEvent event) {

        System.out.println("💰 Processing payment for order: " + event.orderId());

        PaymentApprovedEvent approved =
                new PaymentApprovedEvent(event.orderId(), "APPROVED");

        kafkaTemplate.send("payment-approved", approved);

        System.out.println("✅ Payment approved!");
    }
}
