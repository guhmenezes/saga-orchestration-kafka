package br.com.saga.orchestrator.event;

public record StockReservedEvent(
        String orderId,
        String product,
        int quantity
) {}
