package br.com.saga.orchestrator.core.repository;

import br.com.saga.orchestrator.core.domain.Saga;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SagaRepository extends MongoRepository<Saga,String> {
    Optional<Saga> findByOrderId(String orderId);
}
