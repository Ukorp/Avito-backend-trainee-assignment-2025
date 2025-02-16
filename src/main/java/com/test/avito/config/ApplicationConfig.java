package com.test.avito.config;

import com.test.avito.model.Role;
import com.test.avito.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Lazy
    public User deletedUser() {
        return User.builder()
                .id(0)
                .role(Role.USER)
                .password("deleted")
                .coins(0)
                .username("deleted")
                .build();
    }
}
