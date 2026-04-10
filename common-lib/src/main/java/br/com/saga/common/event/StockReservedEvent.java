package br.com.saga.common.event;

public record StockReservedEvent(
        String orderId,
        String productId,
        int quantity
) {}
