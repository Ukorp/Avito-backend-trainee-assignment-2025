package com.test.avito.mapper.impl;

import com.test.avito.dto.CoinSent;
import com.test.avito.mapper.CoinSentMapper;
import com.test.avito.model.Logs;
import com.test.avito.model.User;
import com.test.avito.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CoinSentMapperImpl implements CoinSentMapper {

    private final UserService userService;
    private final User deletedUser;

    @Override
    public Mono<CoinSent> toCoinSent(Logs logs) {
        return userService.findById(logs.getSecondUserId())
                .switchIfEmpty(Mono.just(deletedUser))
                .map(usr -> CoinSent.builder()
                        .amount(logs.getCoins())
                        .toUser(usr.getUsername())
                        .build());
    }
}
