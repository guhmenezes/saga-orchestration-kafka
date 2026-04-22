package br.com.saga.common.payload;

public record OrderPayload(
        String orderId,
        String productId,
        Integer quantity
) {}