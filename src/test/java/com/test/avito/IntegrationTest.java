package com.test.avito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.avito.dto.auth.AuthResponse;
import com.test.avito.model.Role;
import com.test.avito.model.User;
import com.test.avito.repository.UserRepository;
import com.test.avito.service.UserService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("avito")
            .withUsername("admin")
            .withPassword("1234");
    @Autowired
    private UserService userService;

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = postgresContainer.getJdbcUrl();

        String r2bdcUrl = jdbcUrl.replaceFirst("^jdbc:", "r2dbc:");
        registry.add("spring.r2dbc.url", () -> r2bdcUrl);
        registry.add("spring.r2dbc.username", postgresContainer::getUsername);
        registry.add("spring.r2dbc.password", postgresContainer::getPassword);
        registry.add("spring.flyway.url", () -> jdbcUrl);
        registry.add("spring.flyway.user", postgresContainer::getUsername);
        registry.add("spring.flyway.password", postgresContainer::getPassword);
    }

    @BeforeAll
    static void setUp() {
        // Применяем миграции Flyway
        Flyway flyway = Flyway.configure()
                .dataSource(postgresContainer.getJdbcUrl(), postgresContainer.getUsername(), postgresContainer.getPassword())
                .load();
        flyway.migrate();
    }

    @Test
    void testNotAuthorized() throws Exception {
        webTestClient.get()
                .uri("http://localhost:" + port + "/api/info")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    @WithMockUser(username = "pep@pep.ru", roles = {"USER"})
    void testSendCoinPositive() throws Exception {
        // Создаем тестовых пользователей
        User user1 = User.builder()
                .username("pep@pep.ru")
                .password("peppep")
                .coins(1000)
                .role(Role.USER)
                .build();
        User user2 = User.builder()
                .username("onetwo@example.com")
                .password("three")
                .coins(1000)
                .role(Role.USER)
                .build();
        userService.save(user1).block();
        userService.save(user2).block();

        final String postJson = """
                {
                    "toUser": "onetwo@example.com",
                    "amount": 200
                }
                """;

        webTestClient.post()
                .uri("http://localhost:" + port + "/api/sendCoin")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(postJson)
                .exchange()
                .expectStatus()
                .isOk();

        Mono<Integer> coins1 = userService.findByEmail("pep@pep.ru").map(user -> user.getCoins());
        Mono<Integer> coins2 = userService.findByEmail("onetwo@example.com").map(user -> user.getCoins());

        var coinsTuple = Mono.zip(coins1, coins2);

        StepVerifier.create(coinsTuple)
                .assertNext(tuple -> {
                    assertThat(tuple.getT1()).isEqualTo(800);
                    assertThat(tuple.getT2()).isEqualTo(1200);
                })
                .verifyComplete();
    }

    @Test
    @WithMockUser(username = "negative", roles = {"USER"})
    void testSendCoinNegative() throws Exception {
        // Создаем тестовых пользователей
        User user1 = User.builder()
                .username("negative")
                .password("peppep")
                .coins(1000)
                .role(Role.USER)
                .build();
        User user2 = User.builder()
                .username("negative_receiver")
                .password("three")
                .coins(1000)
                .role(Role.USER)
                .build();
        userService.save(user1).block();
        userService.save(user2).block();

        final String postJson = """
                {
                    "toUser": "negative_receiver",
                    "amount": 1001
                }
                """;

        webTestClient.post()
                .uri("http://localhost:" + port + "/api/sendCoin")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(postJson)
                .exchange()
                .expectStatus()
                .isBadRequest();

        Mono<Integer> coins1 = userService.findByEmail("negative").map(user -> user.getCoins());
        Mono<Integer> coins2 = userService.findByEmail("negative_receiver").map(user -> user.getCoins());

        var coinsTuple = Mono.zip(coins1, coins2);

        StepVerifier.create(coinsTuple)
                .assertNext(tuple -> {
                    assertThat(tuple.getT1()).isEqualTo(1000);
                    assertThat(tuple.getT2()).isEqualTo(1000);
                })
                .verifyComplete();
    }

    @Test
    void testBuyMerchPositive() throws Exception {
        // Создаем тестовых пользователей
        User user1 = User.builder()
                .username("negative")
                .password("peppep")
                .coins(1000)
                .role(Role.USER)
                .build();

        final String registerJson = """
                {
                    "username": "userTest",
                    "password": "user"
                }
                """;

        final String expectedJson = """
            {
                "coins": 990,
                inventory: [
                    {
                        "type": "pen",
                        "quantity": 1
                    }],
                coinHistory: {
                  "received": [],
                  "sent": []
                }
            }
            """;

        AuthResponse tokenJson = webTestClient.post()
                .uri("http://localhost:" + port + "/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerJson)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(AuthResponse.class)
                .getResponseBody().blockFirst();

        webTestClient.post()
                .uri("http://localhost:" + port + "/api/buy/pen")
                .header("Authorization", "Bearer " + tokenJson.getToken())
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.get()
                .uri("http://localhost:" + port + "/api/info")
                .header("Authorization", "Bearer " + tokenJson.getToken())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(expectedJson);
    }

    @Test
    void testBuyMerchNegative() throws Exception {

        final String loginJson = """
                {
                    "username": "userTest",
                    "password": "user"
                }
                """;

        final String expectedJson = """
            {
                "coins": 990,
                inventory: [
                    {
                        "type": "pen",
                        "quantity": 1
                    }],
                coinHistory: {
                  "received": [],
                  "sent": []
                }
            }
            """;

        AuthResponse tokenJson = webTestClient.post()
                .uri("http://localhost:" + port + "/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginJson)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(AuthResponse.class)
                .getResponseBody().blockFirst();

        webTestClient.post()
                .uri("http://localhost:" + port + "/api/buy/jeans")
                .header("Authorization", "Bearer " + tokenJson.getToken())
                .exchange()
                .expectStatus()
                .isBadRequest();

        webTestClient.get()
                .uri("http://localhost:" + port + "/api/info")
                .header("Authorization", "Bearer " + tokenJson.getToken())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(expectedJson);
    }

    @Test
    void testPasswordNegative() throws Exception {
        final String loginJson = """
                {
                    "username": "userTest",
                    "password": "user1"
                }
                """;

        webTestClient.post()
                .uri("http://localhost:" + port + "/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginJson)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
}
