package com.example.cloudstorageservice.security;

import org.springframework.http.HttpStatus;

public interface AuthenticationService { //AuthenticationService

    String getAuthTokenByUsernameAndPassword(String username, String password);

    HttpStatus deleteTokenAndLogout(String token);


}
