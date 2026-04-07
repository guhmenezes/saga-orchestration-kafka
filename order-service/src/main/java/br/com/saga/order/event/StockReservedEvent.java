package br.com.saga.order.event;

public record StockReservedEvent(
        String orderId,
        String product,
        int quantity
) {}