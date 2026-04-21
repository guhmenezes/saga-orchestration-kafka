package br.com.saga.common.event.payment;

import br.com.saga.common.payload.OrderPayload;

public record PaymentApprovedEvent(
        OrderPayload payload
) {}