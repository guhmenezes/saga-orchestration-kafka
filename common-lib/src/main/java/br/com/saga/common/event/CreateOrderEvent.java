package br.com.saga.common.event;

public record CreateOrderEvent(
        String orderId,
        String productId,
        int quantity
) {}
