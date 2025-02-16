package com.test.avito.repository;

import com.test.avito.model.Logs;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface LogRepository extends ReactiveCrudRepository<Logs, Long> {
    Flux<Logs> findAllByUserId(Long userId);

    Flux<Logs> findAllByUserIdAndDetails(long userId, String details);
}
