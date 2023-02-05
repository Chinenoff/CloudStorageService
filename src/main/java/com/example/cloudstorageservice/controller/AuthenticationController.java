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
@CrossOrigin(
        origins = "http://localhost:8080",
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = {RequestMethod.POST, RequestMethod.OPTIONS})
public class AuthenticationController {

    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        final String token = authenticationService.getAuthTokenByUsernameAndPassword(authRequest.getName(), authRequest.getPassword());
        return new AuthResponse(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String userToken) {
        authenticationService.deleteTokenAndLogout(userToken);
        return ResponseEntity.ok(HttpStatus.OK + "Success logout");
    }

}
