package com.test.avito.service;



import com.test.avito.dto.auth.AuthRequest;
import com.test.avito.dto.auth.AuthResponse;
import com.test.avito.exception.WrongPasswordException;
import com.test.avito.model.Role;
import com.test.avito.model.User;
import com.test.avito.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Mono<AuthResponse> register(AuthRequest request) {
        log.info("Registering user: {}", request);
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .coins(1000)
                .build();
        return repository.save(user).map(
                usr -> new AuthResponse(jwtService.generateToken(Map.of("role", usr.getRole()), usr))
        );
    }

    public Mono<AuthResponse> authentication(AuthRequest request) {
        return repository.findByUsername(request.getUsername())
                .flatMap(user -> {
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return Mono.just(new AuthResponse(jwtService.generateToken(Map.of("role", user.getRole()), user)));
                    } else {
                        return Mono.error(new WrongPasswordException("Invalid password"));
                    }
                })
                .switchIfEmpty(register(request));
    }
}
