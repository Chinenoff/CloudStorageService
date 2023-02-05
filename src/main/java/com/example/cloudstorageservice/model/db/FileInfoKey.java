package com.example.cloudstorageservice.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileInfoKey implements Serializable {

    public static final long serialVersionUID = 1L;

    @NotBlank
    @Column(nullable = false)
    private String fileName;

    @NotBlank
    private String username;

}
