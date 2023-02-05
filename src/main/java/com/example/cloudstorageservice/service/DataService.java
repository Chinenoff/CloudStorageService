package com.example.cloudstorageservice.service;

import com.example.cloudstorageservice.model.response.FileInfo;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DataService {
    List<FileInfo> getFileNames();

    HttpStatus uploadFile(String fileName, MultipartFile file) throws IOException;

    HttpStatus editFileName(String currentFileName, String newFileName);

    byte [] getFileByName(String fileName);

    HttpStatus deleteFile(String fileName);
}
