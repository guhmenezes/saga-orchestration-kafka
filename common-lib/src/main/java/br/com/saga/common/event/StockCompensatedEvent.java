package br.com.saga.common.event;

public record StockCompensatedEvent(
        OrderPayload payload
) {}