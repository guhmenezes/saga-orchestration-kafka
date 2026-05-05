package br.com.saga.payment.core.service;

import br.com.saga.common.payload.OrderPayload;
import br.com.saga.payment.core.client.PaymentClient;
import br.com.saga.payment.core.domain.Payment;
import br.com.saga.payment.core.domain.PaymentStatus;
import br.com.saga.payment.core.exceptions.PaymentNotFoundException;
import br.com.saga.payment.core.exceptions.RefusedPaymentException;
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
    private final PaymentStatusService statusService;
    private final PaymentClient paymentClient;

    @Transactional
    public void processPayment(OrderPayload payload) {
        log.info("Processando pagamento para o pedido {}", payload.orderId());

        Payment payment = statusService.createPendingPayment(payload);

        if (payment.getStatus() == PaymentStatus.SUCCESS || payment.getStatus() == PaymentStatus.REFUNDED) {
            log.warn("Pagamento já finalizado para o pedido: {}", payload.orderId());
            return;
        }

        boolean isAuthorized = paymentClient.authorize(payload);

        if (!isAuthorized) {
            statusService.updateToFailed(payload.orderId());
            throw new RefusedPaymentException("Pagamento recusado pelo Gateway.");
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        repository.save(payment);

    }

    @Transactional
    public void refundPayment(OrderPayload payload) {
        log.warn("Solicitação de estorno recebida para o pedido: {}", payload.orderId());

        Payment payment = repository.findByOrderId(payload.orderId())
                .orElseThrow(() -> new PaymentNotFoundException("Pagamento não encontrado para estorno."));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            log.warn("Pagamento já constava como estornado para o pedido: {}", payload.orderId());
            return;
        }

        paymentClient.refund(payload);

        payment.setStatus(PaymentStatus.REFUNDED);
        repository.save(payment);
        log.info("Estorno concluído com sucesso para o pedido: {}", payload.orderId());
    }
}
