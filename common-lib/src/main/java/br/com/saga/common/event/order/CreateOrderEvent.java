package br.com.saga.common.event.order;

import br.com.saga.common.payload.OrderPayload;

public record CreateOrderEvent(
        OrderPayload payload
) {}
