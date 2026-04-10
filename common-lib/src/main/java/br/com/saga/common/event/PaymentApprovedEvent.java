package br.com.saga.common.event;

public record PaymentApprovedEvent(
        String orderId,
        String status
) {}