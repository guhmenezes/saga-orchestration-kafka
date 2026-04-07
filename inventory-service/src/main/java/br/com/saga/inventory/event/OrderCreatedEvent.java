package br.com.saga.inventory.event;

public record OrderCreatedEvent(
        String orderId,
        String product,
        int quantity
) {}