package br.com.saga.common.event.inventory;

import br.com.saga.common.payload.OrderPayload;

public record ReserveStockEvent(
        OrderPayload payload
) {}
