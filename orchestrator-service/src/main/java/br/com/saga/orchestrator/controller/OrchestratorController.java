package br.com.saga.orchestrator.controller;

import br.com.saga.orchestrator.dto.CreateOrderRequestDTO;
import br.com.saga.orchestrator.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrchestratorController {

    private final OrchestratorService service;

    @PostMapping("/order")
    public String startSaga(@RequestBody CreateOrderRequestDTO request) {
        String orderId = service.startSaga(request);

        return "Saga started: " + orderId;
    }
}