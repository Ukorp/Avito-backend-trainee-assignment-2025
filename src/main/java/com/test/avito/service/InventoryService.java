package com.test.avito.service;

import com.test.avito.exception.UserNotFoundException;
import com.test.avito.model.Inventory;
import com.test.avito.repository.InventoryRepository;
import com.test.avito.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final UserRepository userService;

    public Flux<Inventory> findAllByUserId(long userId) {
        return inventoryRepository.findAllByUserId(userId);
    }

    public Flux<Inventory> getUserInventory(String username) {
        return userService.findByUsername(username)
                        .switchIfEmpty(Mono.error(new UserNotFoundException("user not found")))
                        .flatMapMany(usr -> findAllByUserId(usr.getId()));
    }
}
