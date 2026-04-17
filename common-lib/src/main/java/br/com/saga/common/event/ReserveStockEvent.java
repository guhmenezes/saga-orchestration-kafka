package br.com.saga.common.event;

public record ReserveStockEvent(
        OrderPayload payload
) {}
