package br.com.saga.common.event;

public record PaymentRequestEvent(
        OrderPayload payload
) {}
