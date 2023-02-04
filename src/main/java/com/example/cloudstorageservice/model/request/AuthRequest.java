package com.example.cloudstorageservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.netology.cloudstorage.entity.db.UserEntity;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
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
