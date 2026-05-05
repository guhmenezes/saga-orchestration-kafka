package br.com.saga.payment.core.exceptions;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String message) {
        super(message,null,false,false);
    }
}
