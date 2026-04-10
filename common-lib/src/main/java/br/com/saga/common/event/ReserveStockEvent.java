package br.com.saga.common.event;

public record ReserveStockEvent(
        String orderId,
        String productId,
        int quantity
) {}
