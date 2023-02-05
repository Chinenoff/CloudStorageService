package com.example.cloudstorageservice.model.db;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserJwtEntity {

    @Id
    private String jwtToken;
    @Column(nullable = false)
    private String username;

}
