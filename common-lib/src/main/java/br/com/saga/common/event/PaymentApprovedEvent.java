package br.com.saga.common.event;

public record PaymentApprovedEvent(
        OrderPayload payload
) {}