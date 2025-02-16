package com.test.avito.service;

import com.test.avito.model.Logs;
import com.test.avito.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    private final UserService userService;

    public Flux<Logs> findAll(long userId) {
        return logRepository.findAllByUserId(userId);
    }

    public Flux<Logs> findByUserIdTransferTo(long userId) {
        log.debug("trying to find {} with action:{}", userId, "tranfer_to");
        return logRepository.findAllByUserIdAndDetails(userId, "transfer_to");
    }

    public Flux<Logs> findByUserIdReceivedFrom(long userId) {
        log.debug("trying to find {} with action:{}", userId, "transfer_from");
        return logRepository.findAllByUserIdAndDetails(userId, "transfer_from");
    }

    public Flux<Logs> FindAllLogsByUsername(String username) {
        return userService.findByEmail(username)
                .switchIfEmpty(Mono.error(new RuntimeException("user not found")))
                .flatMapMany(usr -> findAll(usr.getId()));
    }
}
