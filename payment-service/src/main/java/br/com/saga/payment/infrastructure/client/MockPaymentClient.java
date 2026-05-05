package br.com.saga.payment.infrastructure.client;

import br.com.saga.common.payload.OrderPayload;
import br.com.saga.payment.core.client.PaymentClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockPaymentClient implements PaymentClient {

    @Override
    public boolean authorize(OrderPayload payload) {
        log.info("Enviando requisição de autorização para o Gateway Pedido: {}.", payload.orderId());
        simulateLatency();

        boolean isAuthorized = payload.quantity() != 9;

        if (isAuthorized) {
            log.info("Pagamento autorizado para o pedido: {}", payload.orderId());
        } else {
            log.warn("Pagamento RECUSADO para o pedido: {}", payload.orderId());
        }

        return isAuthorized;
    }

    @Override
    public boolean refund(OrderPayload payload) {
        log.info("Solicitando estorno para o pedido {} no Gateway externo.", payload.orderId());
        simulateLatency();
        log.info("Estorno confirmado para o pedido: {}.", payload.orderId());
        return true;
    }

    private void simulateLatency() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Erro na simulação de latência", e);
        }
    }
}
