package br.com.saga.inventory.core.service;

import br.com.saga.common.payload.OrderPayload;
import br.com.saga.inventory.core.domain.Inventory;
import br.com.saga.inventory.core.domain.InventoryTransactionType;
import br.com.saga.inventory.core.domain.OrderInventory;
import br.com.saga.inventory.core.exceptions.InsufficientStockException;
import br.com.saga.inventory.core.exceptions.StockNotFoundException;
import br.com.saga.inventory.core.repository.InventoryRepository;
import br.com.saga.inventory.core.repository.OrderInventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repository;
    private final OrderInventoryRepository orderInventoryRepository;

    @Transactional
    public void updateStock(OrderPayload payload) {
        log.info("Atualizando estoque para o produto: {}", payload.productId());

        if (orderInventoryRepository.existsByOrderIdAndTransactionType(payload.orderId(), InventoryTransactionType.RESERVED)) {
            log.warn("Pedido {} já processado no estoque.", payload.orderId());
            return;
        }

        Inventory inventory = repository.findByProductId(payload.productId())
                .orElseThrow(StockNotFoundException::new);

        int available = inventory.getQuantity();
        int requested = payload.quantity();

        if (available < requested) {
            throw new InsufficientStockException(requested, available, payload.productId());
        }

        inventory.setQuantity(available - requested);
        repository.save(inventory);

        saveInventoryLog(payload, InventoryTransactionType.RESERVED);
    }

    @Transactional
    public void rollbackStock(OrderPayload payload) {
        log.warn("Executando compensação de estoque para o pedido: {}", payload.orderId());

        if (orderInventoryRepository.existsByOrderIdAndTransactionType(payload.orderId(), InventoryTransactionType.REFUNDED)) {
            log.warn("Estorno já realizado para o pedido: {}", payload.orderId());
            return;
        }

        Inventory inventory = repository.findByProductId(payload.productId())
                .orElseThrow(StockNotFoundException::new);

        inventory.setQuantity(inventory.getQuantity() + payload.quantity());
        repository.save(inventory);

        saveInventoryLog(payload, InventoryTransactionType.REFUNDED);
    }

    private void saveInventoryLog(OrderPayload payload, InventoryTransactionType transactionType) {
        orderInventoryRepository.saveAndFlush(OrderInventory.builder()
                .orderId(payload.orderId())
                .productId(payload.productId())
                .quantity(payload.quantity())
                .transactionType(transactionType)
                .build());
    }
}
