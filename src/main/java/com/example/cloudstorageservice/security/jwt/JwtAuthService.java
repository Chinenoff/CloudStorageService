package com.example.cloudstorageservice.security.jwt;

import com.example.cloudstorageservice.model.db.UserJwtEntity;
import org.springframework.http.HttpStatus;

public interface JwtAuthService {
    String generateToken(String secret);

    String saveTokenToDataBase(String token, String username);

    boolean validateToken(String token, String username);

    HttpStatus deleteToken(String token);

    UserJwtEntity getUserByToken(String token);
}
