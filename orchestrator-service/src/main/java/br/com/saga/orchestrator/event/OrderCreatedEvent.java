package br.com.saga.orchestrator.event;

public record OrderCreatedEvent(
        String orderId,
        String product,
        int quantity
) {}