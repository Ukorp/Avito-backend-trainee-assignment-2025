package com.test.avito.mapper;

import com.test.avito.dto.InfoResponse;
import com.test.avito.model.User;
import reactor.core.publisher.Mono;

public interface InfoResponseMapper {
    Mono<InfoResponse> toInfoResponse(User user);
}
