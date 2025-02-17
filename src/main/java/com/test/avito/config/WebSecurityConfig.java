package com.test.avito.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.avito.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .exceptionHandling(exceptionHandlingSpec ->
                        exceptionHandlingSpec
                                .authenticationEntryPoint(
                                        (swe, e) ->
                                                handleException(swe, e, HttpStatus.UNAUTHORIZED)
                                )
                                .accessDeniedHandler(
                                        (swe, e) ->
                                                handleException(swe, e, HttpStatus.FORBIDDEN)
                                ))
                .authorizeExchange(authorizeExchangeSpec ->
                        authorizeExchangeSpec.pathMatchers("/api/auth", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .anyExchange().authenticated())
                .build();
    }

    private Mono<Void> handleException(ServerWebExchange exchange, Exception e, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(errorResponse);
        } catch (JsonProcessingException ex) {
            json = "{\"error\": \"Internal Server Error\"}";
        }
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(json.getBytes());
        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }

}
