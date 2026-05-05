package br.com.saga.payment.core.service;

import br.com.saga.common.payload.OrderPayload;
import br.com.saga.payment.core.domain.Payment;
import br.com.saga.payment.core.domain.PaymentStatus;
import br.com.saga.payment.core.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentStatusService {

    private final PaymentRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToFailed(String orderId) {
        Payment payment = repository.findByOrderId(orderId)
                .orElseGet(() -> Payment.builder()
                        .orderId(orderId)
                        .build());

        payment.setStatus(PaymentStatus.FAILED);
        repository.save(payment);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Payment createPendingPayment(OrderPayload payload) {
        // TODO: Implementar lógica de valores monetários e PaymentMethod em versões futuras
        return repository.findByOrderId(payload.orderId())
                .orElseGet(() -> repository.saveAndFlush(Payment.builder()
                        .orderId(payload.orderId())
                        .status(PaymentStatus.PENDING)
                        .build()));
    }
}