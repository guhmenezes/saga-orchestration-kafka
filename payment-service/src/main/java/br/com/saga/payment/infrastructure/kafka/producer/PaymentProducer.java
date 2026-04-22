package br.com.saga.payment.infrastructure.kafka.producer;

import br.com.saga.common.event.payment.PaymentApprovedEvent;
import br.com.saga.common.event.payment.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentApproved(PaymentApprovedEvent event) {
        log.info("Enviando aprovação de pagamento: {}", event.payload().orderId());
        kafkaTemplate.send("payment-approved", event.payload().orderId(), event);
    }

    public void sendPaymentFailed(PaymentFailedEvent event) {
        log.warn("Enviando falha de pagamento: {}", event.payload().orderId());
        kafkaTemplate.send("payment-failed", event.payload().orderId(), event);
    }
}
