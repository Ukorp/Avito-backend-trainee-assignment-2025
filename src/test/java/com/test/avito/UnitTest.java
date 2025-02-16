package com.test.avito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.avito.dto.auth.AuthRequest;
import com.test.avito.model.Inventory;
import com.test.avito.model.Merch;
import com.test.avito.model.Role;
import com.test.avito.model.User;
import com.test.avito.repository.InventoryRepository;
import com.test.avito.repository.LogRepository;
import com.test.avito.repository.MerchRepository;
import com.test.avito.repository.UserRepository;
import com.test.avito.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private InventoryRepository inventoryRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private MerchRepository merchRepository;

    @MockitoBean
    private LogRepository logRepository;

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .coins(1000)
                .username("pep@pep.ru")
                .id(1L)
                .password("peppep")
                .role(Role.USER)
                .build();

        User user2 = User.builder()
                .coins(1000)
                .username("onetwo")
                .id(2L)
                .role(Role.USER)
                .password("three")
                .build();

        Mockito.when(userRepository.findById(user1.getId())).thenReturn(Mono.just(user1));
        Mockito.when(userRepository.findById(user2.getId())).thenReturn(Mono.just(user2));
//        Mockito.when(userRepository.findByEmail("pepper").thenReturn();

        Mockito.when(userRepository.findByUsername(user1.getUsername())).thenReturn(Mono.just(user1));
        Mockito.when(userRepository.findByUsername(user2.getUsername())).thenReturn(Mono.just(user2));
        Mockito.when(userRepository.findAll()).thenReturn(Flux.just(user1, user2));
        Mockito.when(inventoryRepository.findAllByUserId(1L)).thenReturn(Flux.just(
                Inventory.builder().userId(1L).id(1L).itemId(4L).quantity(1).build(),
                Inventory.builder().userId(1L).id(2L).itemId(5L).quantity(3).build(),
                Inventory.builder().userId(1L).id(3L).itemId(6L).quantity(1).build())
                );
        Mockito.when(inventoryRepository.findAllByUserId(2L)).thenReturn(Flux.just(
                        Inventory.builder().userId(2L).id(4L).itemId(1L).quantity(4).build(),
                        Inventory.builder().userId(2L).id(5L).itemId(2L).quantity(4).build(),
                        Inventory.builder().userId(2L).id(6L).itemId(3L).quantity(1).build())
                );

        Mockito.when(logRepository.findAllByUserIdAndDetails(Mockito.anyLong(), Mockito.anyString())).thenReturn(
                Flux.just()
        );
        Mockito.when(logRepository.findAllByUserIdAndDetails(Mockito.anyLong(), Mockito.anyString())).thenReturn(
                Flux.just()
        );

        Mockito.when(merchRepository.findById(1L)).thenReturn(Mono.just(new Merch(1L, "pen", 8L)));
        Mockito.when(merchRepository.findById(2L)).thenReturn(Mono.just(new Merch(2L, "book", 80L)));
        Mockito.when(merchRepository.findById(3L)).thenReturn(Mono.just(new Merch(3L, "notebook", 40L)));
        Mockito.when(merchRepository.findById(4L)).thenReturn(Mono.just(new Merch(4L, "t-shirt", 100L)));
        Mockito.when(merchRepository.findById(5L)).thenReturn(Mono.just(new Merch(5L, "shirt", 120L)));
        Mockito.when(merchRepository.findById(6L)).thenReturn(Mono.just(new Merch(6L, "jacket", 200L)));
        Mockito.when(userRepository.sendMoney(1, 2, 200, "перевод")).thenReturn(Mono.just("Успешно!"));
    }

    @Test
    void testAuthorizedNegative() throws Exception {
        AuthRequest request = new AuthRequest("pep", "pep");
        webTestClient.get()
                .uri("http://localhost:8080/api/info")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    @WithMockUser(username = "pep@pep.ru", value = "pep@pep.ru")
    void testInfoRequestPositive() throws Exception {

        final String expectedResult = """
                {
                    "coins": 1000,
                    inventory: [
                        {
                            "type": "t-shirt",
                            "quantity": 1
                        },
                        {
                            "type": "shirt",
                            "quantity": 3
                        },
                        {
                            "type": "jacket",
                            "quantity": 1
                        }],
                    coinHistory: {
                      "received": [],
                      "sent": []
                    }
                }
                """;

        webTestClient.get()
                .uri("http://localhost:8080/api/info")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(expectedResult);
    }

    @Test
    @WithMockUser(username = "pep@pep.ru", value = "pep@pep.ru")
    void testSendCoinNegative() throws Exception {
        final String postJson = """
                {
                    "toUser": "onetwo",
                    "amount": 200
                }
                """;
        webTestClient.post()
                .uri("http://localhost:8080/api/sendCoin")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(postJson)
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }
}
