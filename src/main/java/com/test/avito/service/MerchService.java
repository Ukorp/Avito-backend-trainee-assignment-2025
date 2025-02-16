package com.test.avito.service;

import com.test.avito.model.Merch;
import com.test.avito.repository.MerchRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class MerchService {

    private final MerchRepository merchRepository;

    public Mono<Merch> findMerchByName(String name) {
        return merchRepository.findByName(name);
    }

    public Mono<Merch> findMerchById(long id) {
        return merchRepository.findById(id);
    }
}
