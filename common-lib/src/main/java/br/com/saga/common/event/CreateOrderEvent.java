package br.com.saga.common.event;

public record CreateOrderEvent(
        OrderPayload payload
) {}
