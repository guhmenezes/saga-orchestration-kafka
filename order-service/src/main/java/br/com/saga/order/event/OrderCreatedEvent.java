package br.com.saga.order.event;

public record OrderCreatedEvent(
        String orderId,
        String product,
        int quantity
) {}