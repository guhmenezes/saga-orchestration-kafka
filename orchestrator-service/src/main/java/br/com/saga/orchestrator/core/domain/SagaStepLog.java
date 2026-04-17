package br.com.saga.orchestrator.core.domain;

import java.time.LocalDateTime;

record SagaStepLog(
        String step,
        String details,
        LocalDateTime timestamp
) {}