package br.com.saga.payment.core.client;

import br.com.saga.common.payload.OrderPayload;

public interface PaymentClient {

    boolean authorize(OrderPayload payload);

    boolean refund(OrderPayload payload);
}
