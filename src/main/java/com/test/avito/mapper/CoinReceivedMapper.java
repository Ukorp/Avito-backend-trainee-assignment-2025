package com.test.avito.mapper;

import com.test.avito.dto.CoinHistory;
import com.test.avito.dto.CoinReceived;
import com.test.avito.model.Logs;
import reactor.core.publisher.Mono;

public interface CoinReceivedMapper {
    Mono<CoinReceived> toCoinReceived(Logs logs);
}
