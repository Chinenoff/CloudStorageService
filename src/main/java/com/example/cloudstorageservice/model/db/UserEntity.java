package com.example.cloudstorageservice.model.db;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
/*@Getter
@Setter*/
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private String username;
    @Column(nullable = false)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
