package com.example.cloudstorageservice.security;

import com.example.cloudstorageservice.model.db.UserEntity;
import com.example.cloudstorageservice.model.exception.InvalidAccessDataException;
import com.example.cloudstorageservice.model.exception.UserNotFoundException;
import com.example.cloudstorageservice.repository.SecurityRepository;
import com.example.cloudstorageservice.security.jwt.JwtAuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AuthenticationServiceImpl.class, CustomUserDetailsService.class})
public class AuthenticationServiceImplTest {

    @MockBean
    private JwtAuthService jwtAuthService;
    @MockBean
    private SecurityRepository securityRepository;
    @Autowired
    private AuthenticationServiceImpl authenticationService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser("user")
    public void getAuthTokenByUsernameAndPasswordSuccessCaseTest() {
        //given
        final String username = "user";
        final String password = "userpass";
        final String tokenExpected = "test_jwt_token";
        Mockito.when(securityRepository.findById(username)).thenReturn(Optional.of(new UserEntity(username, password)));
        Mockito.when(jwtAuthService.generateToken(password + username))
                .thenReturn(tokenExpected);
        Mockito.when(jwtAuthService.saveTokenToDataBase(tokenExpected, username))
                .thenReturn(tokenExpected);
        //when
        final String tokenActual = authenticationService.getAuthTokenByUsernameAndPassword(username, password);
        //then
        Assertions.assertEquals(tokenExpected, tokenActual);

    }

    @Test
    @WithMockUser("user")
    public void getAuthTokenByUsernameAndPasswordThrowInvalidAccessTest() {
        //given
        final String username = "user";
        final String invalidPassword = "test_invalid_pass";
        final String validPass = "test_valid_pass";
        Mockito.when(securityRepository.findById(username)).thenReturn(Optional.of(new UserEntity(username, validPass)));
        //when
        InvalidAccessDataException thrown = Assertions.assertThrows(InvalidAccessDataException.class, () -> {
            authenticationService.getAuthTokenByUsernameAndPassword(username, invalidPassword);
        });
        //then
        Assertions.assertEquals("Invalid username or password", thrown.getMessage());
    }

    @Test
    public void getAuthTokenByUsernameAndPasswordThrowUsernameNotFoundTest() {
        //given
        final String invalidUsername = "test_invalid_user";
        final String password = "password";
        Mockito.when(securityRepository.findById(invalidUsername)).thenReturn(Optional.empty());
        //when
        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            authenticationService.getAuthTokenByUsernameAndPassword(invalidUsername, password);
        });
        //then
        Assertions.assertEquals("User not found", thrown.getMessage());
    }


    @Test
    public void deleteTokenAndLogoutSuccessCaseTest() {
        //given
        final String token = "test_token_bearer";
        final HttpStatus expectedStatus = HttpStatus.OK;
        Mockito.when(jwtAuthService.deleteToken(token.substring(7)))
                .thenReturn(HttpStatus.OK);
        //when
        final HttpStatus actualStatus = authenticationService.deleteTokenAndLogout(token);
        //then
        Assertions.assertEquals(expectedStatus, actualStatus);
    }
}
