package com.test.avito.mapper;

import com.test.avito.dto.CoinHistory;
import com.test.avito.model.User;
import reactor.core.publisher.Mono;

public interface CoinHistoryMapper {
    Mono<CoinHistory> toCoinHistory(User user);
}
