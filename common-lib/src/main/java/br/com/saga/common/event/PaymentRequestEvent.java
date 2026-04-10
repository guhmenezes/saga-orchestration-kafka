package br.com.saga.common.event;

public record PaymentRequestEvent(
        String orderId,
        String productId,
        Integer quantity
) {}
