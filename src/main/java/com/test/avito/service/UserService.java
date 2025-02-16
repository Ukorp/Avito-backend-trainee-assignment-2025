package com.test.avito.service;

import com.test.avito.model.User;
import com.test.avito.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> findByEmail(String username) {
        return userRepository.findByUsername(username);
    }

    public Mono<Void> deleteById(Long id) {
        return userRepository.deleteById(id);
    }

//    @Transactional
    public Mono<String> sendMoney(Long senderId, Long receiverId, long amount) {
        return userRepository.sendMoney(senderId, receiverId, amount, "Перевод");
    }

//    @Transactional
    public Mono<String> buyMerch(long userId, long merchId) {
        return userRepository.buyMerch(userId, merchId);
    }
}
