package br.com.saga.inventory.core.service;

import br.com.saga.common.payload.OrderPayload;
import br.com.saga.inventory.core.domain.Inventory;
import br.com.saga.inventory.core.exceptions.InsufficientStockException;
import br.com.saga.inventory.core.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repository;

    @Transactional
    public void updateStock(OrderPayload payload) {
        log.info("Atualizando estoque para o produto: {}", payload.productId());

        Inventory inventory = repository.findByProductId(payload.productId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado no estoque"));

        int available = inventory.getQuantity();
        int requested = payload.quantity();

        if (available < requested) {
            throw new InsufficientStockException(requested, available, payload.productId());
        }

        inventory.setQuantity(available - requested);
        repository.save(inventory);
    }

    @Transactional
    public void rollbackStock(OrderPayload payload) {
        log.warn("Executando compensação de estoque para o pedido: {}", payload.orderId());

        Inventory inventory = repository.findByProductId(payload.productId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado para rollback"));

        inventory.setQuantity(inventory.getQuantity() + payload.quantity());
        repository.save(inventory);
    }
}
