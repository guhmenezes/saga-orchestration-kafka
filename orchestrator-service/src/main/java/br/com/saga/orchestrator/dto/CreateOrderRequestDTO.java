package br.com.saga.orchestrator.dto;

public record CreateOrderRequestDTO(
        String productId,
        Integer quantity
) {}
