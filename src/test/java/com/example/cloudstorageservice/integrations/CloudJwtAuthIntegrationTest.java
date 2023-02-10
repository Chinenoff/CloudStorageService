package com.example.cloudstorageservice.integrations;

import com.example.cloudstorageservice.model.db.UserJwtEntity;
import com.example.cloudstorageservice.model.exception.InvalidJwtException;
import com.example.cloudstorageservice.model.exception.UserNotFoundException;
import com.example.cloudstorageservice.repository.JwtSecurityRepository;
import com.example.cloudstorageservice.security.filters.TokenAuthenticationFilter;
import com.example.cloudstorageservice.security.jwt.JwtAuthServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.servlet.ServletException;
import java.io.IOException;

import static com.example.cloudstorageservice.security.filters.TokenAuthenticationFilter.JWT_REQUEST_HEADER;


@SpringBootTest
@Testcontainers
public class CloudJwtAuthIntegrationTest {

    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;
    @Autowired
    private JwtAuthServiceImpl jwtAuthService;
    @Autowired
    private JwtSecurityRepository jwtSecurityRepository;

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
    public void doFilterIntegrationSuccessTest() throws IOException, ServletException {
        //given
        final String username = "user";
        final String password = "userpass";
        final String dbToken = jwtAuthService.generateToken(password + username);
        final String headerToken = String.format("Bearer %s", dbToken);
        jwtSecurityRepository.save(new UserJwtEntity(dbToken, username));
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JWT_REQUEST_HEADER, headerToken);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain mockFilterChain = new MockFilterChain();
        //when
        tokenAuthenticationFilter.doFilter(request, response, mockFilterChain);
        final String actualResult = SecurityContextHolder.getContext().getAuthentication().getName();
        //then
        Assertions.assertEquals(username, actualResult);
    }

    @Test
    @Transactional
    public void doFilterIntegrationUserHaveNotJwtExceptionTest() throws IOException, ServletException {
        //given
        final String username = "user";
        final String password = "userpass";
        final String dbToken = jwtAuthService.generateToken(password + username);
        final String headerToken = String.format("Bearer %s", dbToken);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JWT_REQUEST_HEADER, headerToken);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain mockFilterChain = new MockFilterChain();
        //when
        InvalidJwtException thrown = Assertions.assertThrows(InvalidJwtException.class, () -> {
            tokenAuthenticationFilter.doFilter(request, response, mockFilterChain);
        });
        //then
        Assertions.assertEquals("User haven't jwt-token", thrown.getMessage());
    }


    @Test
    @Transactional
    public void doFilterIntegrationJwtValidationExceptionTest() throws IOException, ServletException {
        //given
        final String username = "user";
        final String invalidSecret = "invalid_secret";
        final String dbToken = jwtAuthService.generateToken(invalidSecret);
        final String headerToken = String.format("Bearer %s", dbToken);
        jwtSecurityRepository.save(new UserJwtEntity(dbToken, username));
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JWT_REQUEST_HEADER, headerToken);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain mockFilterChain = new MockFilterChain();
        //when
        InvalidJwtException thrown = Assertions.assertThrows(InvalidJwtException.class, () -> {
            tokenAuthenticationFilter.doFilter(request, response, mockFilterChain);
        });
        //then
        Assertions.assertEquals("Invalid token", thrown.getMessage());
    }

    @Test
    @Transactional
    public void doFilterIntegrationUSerNotFoundExceptionTest() throws IOException, ServletException {
        //given
        final String username = "user";
        final String invalidUser = "invalidUser";
        final String password = "userpass";
        final String dbToken = jwtAuthService.generateToken(password + username);
        final String headerToken = String.format("Bearer %s", dbToken);
        jwtSecurityRepository.save(new UserJwtEntity(dbToken, invalidUser));
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JWT_REQUEST_HEADER, headerToken);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain mockFilterChain = new MockFilterChain();
        //when
        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            tokenAuthenticationFilter.doFilter(request, response, mockFilterChain);
        });
        //then
        Assertions.assertEquals("User not found", thrown.getMessage());
    }

}