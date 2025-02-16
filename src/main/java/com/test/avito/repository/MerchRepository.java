package com.test.avito.repository;

import com.test.avito.model.Merch;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@NonNullApi
public interface MerchRepository extends ReactiveCrudRepository<Merch, Long> {
    Mono<Merch> findByName(String merchName);

    Mono<Merch> findById(Long id);
}
