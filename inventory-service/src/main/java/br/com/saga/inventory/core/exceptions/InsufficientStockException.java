package br.com.saga.inventory.core.exceptions;

import lombok.Getter;

@Getter
public class InsufficientStockException extends RuntimeException {
    private final int requested;
    private final int available;
    private final String productId;

    public InsufficientStockException(int requested, int available, String productId) {
        super(String.format("Estoque insuficiente para o produto %s: solicitado %d, disponível %d",
                productId, requested, available));
        this.requested = requested;
        this.available = available;
        this.productId = productId;
    }
}
