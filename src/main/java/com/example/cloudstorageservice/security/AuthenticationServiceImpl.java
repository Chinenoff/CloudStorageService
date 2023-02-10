package com.example.cloudstorageservice.security;

import com.example.cloudstorageservice.model.exception.InvalidAccessDataException;
import com.example.cloudstorageservice.security.jwt.JwtAuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtAuthService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public String getAuthTokenByUsernameAndPassword(String username, String password) {
        if (validateUser(username, password)) {
            String token = jwtService.generateToken(password + username);
            return jwtService.saveTokenToDataBase(token, username);
        }
        throw new InvalidAccessDataException("Invalid username or password");
    }

    @Override
    public HttpStatus deleteTokenAndLogout(String token) {
        return jwtService.deleteToken(token.substring(7));
    }

    private boolean validateUser(String username, String password) {
        log.info("Search for a user in the database by the specified login {}", username);
        UserDetails currentUser = userDetailsService.loadUserByUsername(username);
        return username.equals(currentUser.getUsername()) && password.equals(currentUser.getPassword());
    }
}
