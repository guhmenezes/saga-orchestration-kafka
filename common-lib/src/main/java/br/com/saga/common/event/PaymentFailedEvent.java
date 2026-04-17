package br.com.saga.common.event;

public record PaymentFailedEvent(
        OrderPayload payload,
        String reason
) {}
