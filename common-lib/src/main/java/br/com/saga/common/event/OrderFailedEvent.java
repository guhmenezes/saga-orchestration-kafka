package br.com.saga.common.event;

public record OrderFailedEvent(
        OrderPayload payload,
        String reason
) {}
