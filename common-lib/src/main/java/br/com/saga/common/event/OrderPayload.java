package br.com.saga.common.event;

public record OrderPayload(
        String orderId,
        String productId,
        Integer quantity
) {}