package com.test.avito.controller;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.test.avito.dto.ErrorResponse;
import com.test.avito.dto.auth.AuthRequest;
import jakarta.validation.Valid;
import com.test.avito.dto.auth.AuthResponse;
import com.test.avito.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/auth")
    public Mono<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        return authenticationService.authentication(authRequest);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponse> jsonHandleException(Exception e) {
        return Mono.just(new ErrorResponse("Wrong request body"));
    }
}
