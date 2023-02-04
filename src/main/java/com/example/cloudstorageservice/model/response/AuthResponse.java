package com.example.cloudstorageservice.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthResponse {

    @JsonProperty("auth-token")
    private String authToken;
//TODO
}
