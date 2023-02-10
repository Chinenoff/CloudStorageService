package com.example.cloudstorageservice.model.request;

import com.example.cloudstorageservice.model.db.UserEntity;
import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthRequest {
    @NotBlank
    private String login;
    @NotBlank
    private String password;

    public AuthRequest(UserEntity entity) {
        this.login = entity.getUsername();
        this.password = entity.getPassword();
    }


}
