package com.example.cloudstorageservice.model.request;

import com.example.cloudstorageservice.model.db.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AuthRequest {
    private String name;
    private String password;
}
