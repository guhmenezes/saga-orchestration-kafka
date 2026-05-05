package br.com.saga.payment.core.exceptions;

public class RefusedPaymentException extends RuntimeException {
    public RefusedPaymentException(String message) {
        super(message,null,false,false);
    }
}
