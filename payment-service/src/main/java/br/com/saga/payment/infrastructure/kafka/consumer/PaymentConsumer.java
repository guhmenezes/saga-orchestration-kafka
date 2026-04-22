package br.com.saga.payment.infrastructure.kafka.consumer;

import br.com.saga.common.event.payment.PaymentApprovedEvent;
import br.com.saga.common.event.payment.PaymentFailedEvent;
import br.com.saga.common.event.payment.PaymentRequestEvent;
import br.com.saga.payment.core.service.PaymentService;
import br.com.saga.payment.infrastructure.kafka.producer.PaymentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService service;
    private final PaymentProducer producer;

    @KafkaListener(topics = "process-payment", groupId = "payment-group")
    public void consumeProcess(PaymentRequestEvent event) {
        log.info("Recebido comando de pagamento: {}", event.payload().orderId());
        try {
            service.processPayment(event.payload());
            producer.sendPaymentApproved(new PaymentApprovedEvent(event.payload()));
        } catch (Exception e) {
            log.error("Erro no pagamento: {}", e.getMessage());
            producer.sendPaymentFailed(new PaymentFailedEvent(event.payload(), e.getMessage()));
        }
    }
}
