package com.example.cloudstorageservice.model.db;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
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
