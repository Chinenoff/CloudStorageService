package com.example.cloudstorageservice.integrations;

import com.example.cloudstorageservice.model.db.UserJwtEntity;
import com.example.cloudstorageservice.repository.JwtSecurityRepository;
import com.example.cloudstorageservice.security.AuthenticationService;
import com.example.cloudstorageservice.security.jwt.JwtAuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;


@SpringBootTest
@Testcontainers
public class BasicAuthIntegrationTest {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private JwtSecurityRepository jwtSecurityRepository;
    @Autowired
    private JwtAuthService jwtAuthService;

    @Container
    public static PostgreSQLContainer<?> postgresSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("db")
            .withUsername("postgres")
            .withPassword("pass");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> String.format("jdbc:postgresql://localhost:%d/db", postgresSQLContainer.getFirstMappedPort()));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "pass");
        registry.add("spring.liquibase.enabled", () -> true);
    }


    @Test
    @Transactional
    public void loginIntegrationSuccessTest() {

        //given
        final String username = "user";
        final String password = "userpass";
        //when
        final String apiToken = authenticationService.getAuthTokenByUsernameAndPassword(username, password);
        final UserJwtEntity user = jwtSecurityRepository.getById(apiToken);
        //then
        Assertions.assertEquals(new UserJwtEntity(apiToken, username), user);
    }

    @Test
    @Transactional
    public void deleteTokenAndLogoutSuccessCase() {
        //given
        final String username = "user";
        final String password = "userpass";
        final String dbToken = jwtAuthService.generateToken(password + username);
        final String headerToken = String.format("Bearer %s", dbToken);
        jwtSecurityRepository.save(new UserJwtEntity(dbToken, password));
        //when
        authenticationService.deleteTokenAndLogout(headerToken);
        Optional<UserJwtEntity> expected = jwtSecurityRepository.findDistinctByJwtToken(dbToken);
        //then
        Assertions.assertFalse(expected.isPresent());
    }


}

