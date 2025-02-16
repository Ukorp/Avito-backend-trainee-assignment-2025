package com.test.avito.mapper;

import com.test.avito.dto.CoinSent;
import com.test.avito.model.Logs;
import reactor.core.publisher.Mono;

public interface CoinSentMapper{
    Mono<CoinSent> toCoinSent(Logs logs);
}
