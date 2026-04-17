package br.com.saga.orchestrator.core.domain;

public enum SagaStatus {
    STARTED,
    ORDER_SUCCESS,
    ORDER_FAIL,
    INVENTORY_SUCCESS,
    INVENTORY_FAIL,
    PAYMENT_SUCCESS,
    PAYMENT_FAIL,
    COMPENSATING,
    COMPENSATED,
    SAGA_FINISHED,
    SAGA_FAILED
}
