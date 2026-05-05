package br.com.saga.inventory.core.repository;

import br.com.saga.inventory.core.domain.InventoryTransactionType;
import br.com.saga.inventory.core.domain.OrderInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderInventoryRepository extends JpaRepository<OrderInventory,Long> {
    boolean existsByOrderIdAndTransactionType(String orderId, InventoryTransactionType reserved);
}
