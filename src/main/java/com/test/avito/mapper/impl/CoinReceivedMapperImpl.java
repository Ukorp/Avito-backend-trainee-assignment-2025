package com.test.avito.mapper.impl;

import com.test.avito.dto.CoinReceived;
import com.test.avito.mapper.CoinReceivedMapper;
import com.test.avito.model.Logs;
import com.test.avito.model.User;
import com.test.avito.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CoinReceivedMapperImpl implements CoinReceivedMapper {

    private final UserService userService;
    private final User deletedUser;

    @Override
    public Mono<CoinReceived> toCoinReceived(Logs logs) {
        return userService.findById(logs.getSecondUserId())
                .switchIfEmpty(Mono.just(deletedUser))
                .map(usr -> CoinReceived.builder()
                        .amount(logs.getCoins())
                        .fromUser(usr.getUsername())
                        .build());
    }
}
