package br.com.saga.payment.infrastructure.kafka.consumer;

import br.com.saga.common.event.payment.PaymentApprovedEvent;
import br.com.saga.common.event.payment.PaymentFailedEvent;
import br.com.saga.common.event.payment.PaymentRefundedEvent;
import br.com.saga.common.event.payment.PaymentRequestEvent;
import br.com.saga.payment.core.service.PaymentService;
import br.com.saga.payment.infrastructure.kafka.producer.PaymentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService service;
    private final PaymentProducer producer;

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(topics = "process-payment")
    public void consumeProcess(PaymentRequestEvent event) throws InterruptedException {
        service.processPayment(event.payload());
        producer.sendPaymentApproved(new PaymentApprovedEvent(event.payload()));
    }

    @DltHandler
    public void handleProcessDlt(PaymentRequestEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("SAGA CRÍTICA: Falha definitiva no PAGAMENTO. Pedido {} no tópico {}",
                event.payload().orderId(), topic);
        producer.sendPaymentFailed(new PaymentFailedEvent(event.payload(), "Falha após retries no pagamento"));
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(topics = "refund-payment")
    public void consumeRefund(PaymentRequestEvent event) {
        service.refundPayment(event.payload());
        producer.sendPaymentRefunded(new PaymentRefundedEvent(event.payload()));
    }

    @DltHandler
    public void handleRefundDlt(PaymentRequestEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        producer.sendOpsAlert(event.payload(), "FALHA DEFINITIVA NO ESTORNO AUTOMÁTICO");
        producer.sendPaymentFailed(new PaymentFailedEvent(event.payload(), "REFUND_FAILED"));
    }
}
