package br.com.saga.inventory.event;

public record StockReservedEvent(
        String orderId,
        String product,
        int quantity
) {}