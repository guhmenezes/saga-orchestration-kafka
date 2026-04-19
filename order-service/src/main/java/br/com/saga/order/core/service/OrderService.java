package br.com.saga.order.core.service;

import br.com.saga.common.event.OrderPayload;
import br.com.saga.order.core.domain.Order;
import br.com.saga.order.core.domain.OrderStatus;
import br.com.saga.order.core.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;

    @Transactional
    public void createOrder(OrderPayload payload) {
        log.info("Salvando novo pedido no banco: {}", payload.orderId());

        Order order = Order.builder()
                .orderId(payload.orderId())
                .productId(payload.productId())
                .quantity(payload.quantity())
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(order);
    }

    @Transactional
    public void completeOrder(String orderId) {
        log.info("Finalizando pedido com sucesso: {}", orderId);
        updateStatus(orderId, OrderStatus.SUCCESS);
    }

    @Transactional
    public void cancelOrder(String orderId) {
        log.warn("Cancelando pedido (Compensação): {}", orderId);
        updateStatus(orderId, OrderStatus.CANCELLED);
    }

    private void updateStatus(String orderId, OrderStatus status) {
        Order order = repository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + orderId));

        order.setStatus(status);
        repository.save(order);
    }
}
