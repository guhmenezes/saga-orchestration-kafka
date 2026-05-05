package br.com.saga.inventory.core.exceptions;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException() {
        super("Produto não encontrado");
    }
}
