package br.com.saga.common.event;

public record OrderCreatedEvent(
        String orderId,
        String productId,
        int quantity
) {}