package br.com.saga.payment.core.service;

import br.com.saga.common.payload.OrderPayload;
import br.com.saga.payment.core.domain.Payment;
import br.com.saga.payment.core.domain.PaymentStatus;
import br.com.saga.payment.core.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;

    @Transactional(noRollbackFor = RuntimeException.class)
    public void processPayment(OrderPayload payload) {
        log.info("Processando pagamento para o pedido {}", payload.orderId());

        PaymentStatus status = (payload.quantity() == 9) ? PaymentStatus.FAILED : PaymentStatus.SUCCESS;

        // TODO: Implementar lógica de valores monetários e PaymentMethod em versões futuras
        Payment payment = Payment.builder()
                .orderId(payload.orderId())
                .status(PaymentStatus.SUCCESS)
                .build();

        repository.save(payment);

        if (status == PaymentStatus.FAILED) {
            throw new RuntimeException("Simulação de falha: Pagamento recusado.");
        }
    }

    @Transactional(noRollbackFor = RuntimeException.class)
    public void refundPayment(String orderId) {
        log.warn("Estornando pagamento para o pedido: {}", orderId);

        Payment payment = repository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado para estorno"));

        payment.setStatus(PaymentStatus.REFUNDED);
        repository.save(payment);
    }
}
