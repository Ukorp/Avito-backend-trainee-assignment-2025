package com.test.avito.config;

import com.test.avito.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return Mono.empty();
        }
        String authToken = authentication.getPrincipal().toString();
        String username;
        try {
            username = jwtService.extractUsername(authToken);
        }
        catch (Exception e) {
            return Mono.empty();
        }
        if (username != null && jwtService.validateToken(authToken)) {
            String claims = jwtService.extractClaim(authToken, token -> token.get("role", String.class));
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(claims));
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
            return Mono.just(auth);

        } else {
            return Mono.empty();
        }

    }
}
