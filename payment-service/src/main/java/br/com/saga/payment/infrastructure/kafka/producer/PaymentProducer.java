package br.com.saga.payment.infrastructure.kafka.producer;

import br.com.saga.common.event.payment.PaymentApprovedEvent;
import br.com.saga.common.event.payment.PaymentFailedEvent;
import br.com.saga.common.event.payment.PaymentRefundedEvent;
import br.com.saga.common.payload.OrderPayload;
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

    public void sendPaymentRefunded(PaymentRefundedEvent event) {
        log.warn("Enviando status de estorno de pagamento: {}", event.payload().orderId());
        kafkaTemplate.send("payment-refunded", event.payload().orderId(), event);
    }

    public void sendOpsAlert(OrderPayload payload, String reason) {
        log.error("[NOTIFICAÇÃO CRÍTICA - TIME DE OPERAÇÕES]");
        log.error("MOTIVO: {}", reason);
        log.error("DETALHES DO PEDIDO: ID {} | Produto: {} | Quantidade: {}",
                payload.orderId(), payload.productId(), payload.quantity());
        log.error("Ação necessária: Realizar estorno manual no Gateway de Pagamento.");
    }
}
