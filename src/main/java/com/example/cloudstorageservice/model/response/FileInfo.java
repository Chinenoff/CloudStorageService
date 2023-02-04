package com.example.cloudstorageservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileInfo {

    private String filename;
    private Long size;

}
