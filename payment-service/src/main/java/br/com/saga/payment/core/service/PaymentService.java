package br.com.saga.payment.core.service;

import br.com.saga.common.event.OrderPayload;
import br.com.saga.payment.core.domain.Payment;
import br.com.saga.payment.core.domain.PaymentStatus;
import br.com.saga.payment.core.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;

    @Transactional
    public void processPayment(OrderPayload payload) {
        log.info("Processando pagamento para o pedido {}", payload.orderId());

        // TODO: Implementar lógica de valores monetários e PaymentMethod em versões futuras
        Payment payment = Payment.builder()
                .orderId(payload.orderId())
                .status(PaymentStatus.SUCCESS)
                .build();

        repository.save(payment);
    }

    @Transactional
    public void refundPayment(String orderId) {
        log.warn("Estornando pagamento para o pedido: {}", orderId);

        Payment payment = repository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado para estorno"));

        payment.setStatus(PaymentStatus.REFUNDED);
        repository.save(payment);
    }
}
