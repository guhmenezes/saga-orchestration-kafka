package br.com.saga.common.event;

public record OrderCreatedEvent(
        OrderPayload payload
) {}