package com.test.avito.mapper.impl;

import com.test.avito.dto.CoinHistory;
import com.test.avito.mapper.CoinHistoryMapper;
import com.test.avito.mapper.CoinReceivedMapper;
import com.test.avito.mapper.CoinSentMapper;
import com.test.avito.model.User;
import com.test.avito.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CoinHistoryMapperImpl implements CoinHistoryMapper {

    private final LogService logService;

    private final CoinReceivedMapper coinReceivedMapper;

    private final CoinSentMapper coinSentMapper;

    @Override
    public Mono<CoinHistory> toCoinHistory(User user) {
        return logService
                .findByUserIdReceivedFrom(user.getId())
                .flatMap(coinReceivedMapper::toCoinReceived)
                .collectList()
                .zipWith(logService.findByUserIdTransferTo(user.getId())
                        .flatMap(coinSentMapper::toCoinSent)
                        .collectList())
                .map(tuple -> CoinHistory.builder()
                        .received(tuple.getT1())
                        .sent(tuple.getT2())
                        .build());
    }
}
