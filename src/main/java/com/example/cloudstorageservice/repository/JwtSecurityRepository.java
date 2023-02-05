package com.example.cloudstorageservice.repository;

import com.example.cloudstorageservice.model.db.UserJwtEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtSecurityRepository extends JpaRepository<UserJwtEntity, String> {
    Optional<UserJwtEntity> findDistinctByJwtToken(String jwtToken);
}
