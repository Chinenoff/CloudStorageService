package com.example.cloudstorageservice.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FileInfoKey.class)
public class FileInfoEntity {

    @Id
    private String fileName;
    @Id
    private String username;
    @Column(nullable = false)
    private byte[] file;
    @Column(nullable = false)
    private Long size;

}
