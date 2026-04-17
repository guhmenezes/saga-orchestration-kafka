package br.com.saga.orchestrator.api.dto;

public record CreateOrderRequestDTO(
        String productId,
        Integer quantity
) {}
