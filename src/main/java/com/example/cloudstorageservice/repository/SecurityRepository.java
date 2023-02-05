package com.example.cloudstorageservice.repository;

import com.example.cloudstorageservice.model.db.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityRepository extends JpaRepository<UserEntity, String> {
}
