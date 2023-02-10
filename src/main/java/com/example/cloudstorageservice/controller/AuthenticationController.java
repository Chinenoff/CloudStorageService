package com.example.cloudstorageservice.controller;

import com.example.cloudstorageservice.model.request.AuthRequest;
import com.example.cloudstorageservice.model.response.AuthResponse;
import com.example.cloudstorageservice.security.AuthenticationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@Validated
@RestController

public class AuthenticationController {

    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        final String token = authenticationService.getAuthTokenByUsernameAndPassword(authRequest.getLogin(), authRequest.getPassword());
        return new AuthResponse(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String userToken) {
        authenticationService.deleteTokenAndLogout(userToken);
        return ResponseEntity.ok(HttpStatus.OK + "Success logout");
    }

}
