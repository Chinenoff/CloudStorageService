package com.example.cloudstorageservice.service;

import com.example.cloudstorageservice.model.db.FileInfoEntity;
import com.example.cloudstorageservice.model.db.FileInfoKey;
import com.example.cloudstorageservice.model.response.FileInfo;
import com.example.cloudstorageservice.repository.FilesRepository;
import com.example.cloudstorageservice.security.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DataServiceImpl implements DataService {
    private final FilesRepository filesRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public List<FileInfo> getFileNames() {
        log.info("List of user's files requested {}", customUserDetailsService.getUsernameByContext());
        return filesRepository.findByName(customUserDetailsService.getUsernameByContext());
    }

    @Override
    public HttpStatus uploadFile(String fileName, MultipartFile file) throws IOException {
        log.info("Attempt to upload file {} by user {}", fileName, customUserDetailsService.getUsernameByContext());
        FileInfoEntity userFile = FileInfoEntity.builder()
                .fileName(fileName)
                .username(customUserDetailsService.getUsernameByContext())
                .file(file.getBytes())
                .size(file.getSize())
                .build();
        filesRepository.saveAndFlush(userFile);
        log.info("Data loaded");
        return HttpStatus.OK;
    }

    @Override
    public HttpStatus editFileName(String currentFileName, String newFileName) {
        log.info("Attempt to change filename {} by user {}", currentFileName, customUserDetailsService.getUsernameByContext());
        filesRepository.editFileName(customUserDetailsService.getUsernameByContext(), currentFileName, newFileName);
        log.info("Filename successfully changed to {}", newFileName);
        return HttpStatus.OK;
    }

    @Override
    public byte[] getFileByName(String fileName) {
        log.info("File request {} by user {}", fileName, customUserDetailsService.getUsernameByContext());
        return filesRepository.getById(new FileInfoKey(fileName, customUserDetailsService.getUsernameByContext())).getFile();
    }

    @Override
    public HttpStatus deleteFile(String fileName) {
        log.info("Attempt to delete file {} by user {}", fileName, customUserDetailsService.getUsernameByContext());
        filesRepository.deleteById(new FileInfoKey(fileName, customUserDetailsService.getUsernameByContext()));
        log.info("File deleted successfully");
        return HttpStatus.OK;
    }
}
