package br.com.saga.orchestrator.api.controller;

import br.com.saga.orchestrator.api.dto.CreateOrderRequestDTO;
import br.com.saga.orchestrator.core.service.SagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrchestratorController {

    private final SagaService service;

    @PostMapping("/order")
    public String startSaga(@RequestBody CreateOrderRequestDTO request) {
        String orderId = service.startSaga(request);

        return "Saga started: " + orderId;
    }
}