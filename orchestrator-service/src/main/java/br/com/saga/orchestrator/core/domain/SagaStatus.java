package br.com.saga.orchestrator.core.domain;

public enum SagaStatus {
    SAGA_STARTED,
    ORDER_SUCCESS,
    ORDER_FAIL,
    INVENTORY_SUCCESS,
    INVENTORY_FAIL,
    PAYMENT_SUCCESS,
    PAYMENT_FAIL,
    COMPENSATING,
    SAGA_COMPENSATED,
    SAGA_FINISHED,
    SAGA_FAILED
}
