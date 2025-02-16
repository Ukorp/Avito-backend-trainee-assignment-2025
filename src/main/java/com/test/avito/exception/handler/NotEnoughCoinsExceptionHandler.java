package com.test.avito.exception.handler;

import com.test.avito.dto.ErrorResponse;
import com.test.avito.exception.NotEnoughCoinsException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
@Order(1)
public class NotEnoughCoinsExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(NotEnoughCoinsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponse> handleException(Exception e) {
        logger.error(e.getMessage());
        return Mono.just(new ErrorResponse(e.getMessage()));
    }
}
