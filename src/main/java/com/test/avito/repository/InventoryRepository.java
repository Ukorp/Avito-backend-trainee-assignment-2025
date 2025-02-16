package com.test.avito.repository;

import com.test.avito.model.Inventory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface InventoryRepository extends ReactiveCrudRepository<Inventory, Long> {

    Flux<Inventory> findAllByUserId(Long userId);
}
