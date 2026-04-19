package br.com.saga.common.event;

public record StockFailedEvent(
        OrderPayload payload,
        String reason
) {}
