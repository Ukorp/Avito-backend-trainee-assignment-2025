package com.test.avito;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableR2dbcRepositories
@Slf4j
public class AvitoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvitoApplication.class, args);
    }

}
