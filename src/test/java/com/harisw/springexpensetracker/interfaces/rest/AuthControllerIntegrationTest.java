package com.harisw.springexpensetracker.interfaces.rest;

import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Testcontainers
public class AuthControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");


    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    UserRepository userRepository;

    @DynamicPropertySource
    static void overrideJwtSecret(DynamicPropertyRegistry registry) {
        registry.add("spring.jwt.secret", () -> "MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_shouldReturn201WithTokens() {
        var body = Map.of("email", "john@example.com",
                "name", "john",
                "password", "password123");

        restTestClient.post()
                .uri("/api/auth/register")
                .body(body)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("accessToken").isNotEmpty()
                .jsonPath("refreshToken").isNotEmpty()
                .jsonPath("userPublicId").isNotEmpty();
    }

    @Test
    void register_shouldFailWhenRequestInvalid() {
        var body = Map.of("email", "john.com",
                "name", "john",
                "password", "password123");

        restTestClient.post()
                .uri("/api/auth/register")
                .body(body)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void register_shouldFailWhenEmailDuplicates() {
        userRepository.save(new User(null, "john@example.com", "john", UUID.randomUUID(), Instant.now()),
                "password_hash");

        var body = Map.of("email", "john@example.com",
                "name", "john",
                "password", "password123");

        restTestClient.post()
                .uri("/api/auth/register")
                .body(body)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void login_shouldReturn200WithToken() {
        var body = Map.of("email", "john@example.com",
                "name", "john",
                "password", "password123");

        restTestClient.post()
                .uri("/api/auth/register")
                .body(body)
                .exchange();

        restTestClient.post()
                .uri("/api/auth/login")
                .body(Map.of("email", "john@example.com",
                        "password", "password123"))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("accessToken").isNotEmpty()
                .jsonPath("refreshToken").isNotEmpty()
                .jsonPath("userPublicId").isNotEmpty();
    }

    @Test
    void login_shouldReturn400WhenSendIncorrectPassword() {
        var body = Map.of("email", "john@example.com",
                "name", "john",
                "password", "password123");

        restTestClient.post()
                .uri("/api/auth/register")
                .body(body)
                .exchange();

        restTestClient.post()
                .uri("/api/auth/login")
                .body(Map.of("email", "john@example.com",
                        "password", "password"))
                .exchange()
                .expectStatus().is4xxClientError();
    }
}
