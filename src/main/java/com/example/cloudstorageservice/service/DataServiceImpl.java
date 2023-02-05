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
        log.info("Запрошен список файлов пользователя {}", customUserDetailsService.getUsernameByContext());
        return filesRepository.findByName(customUserDetailsService.getUsernameByContext());
    }

    @Override
    public HttpStatus uploadFile(String fileName, MultipartFile file) throws IOException {
        log.info("Попытка загрузки файла {} пользователем {}", fileName, customUserDetailsService.getUsernameByContext());
        FileInfoEntity userFile = FileInfoEntity.builder()
                .fileName(fileName)
                .username(customUserDetailsService.getUsernameByContext())
                .file(file.getBytes())
                .size(file.getSize())
                .build();
        filesRepository.saveAndFlush(userFile);
        log.info("Данные загружены");
        return HttpStatus.OK;
    }

    @Override
    public HttpStatus editFileName(String currentFileName, String newFileName) {
        log.info("Попытка изменения имени файла {} пользователем {}", currentFileName, customUserDetailsService.getUsernameByContext());
        filesRepository.editFileName(customUserDetailsService.getUsernameByContext(), currentFileName, newFileName);
        log.info("Имя файла успешно изменено на {}", newFileName);
        return HttpStatus.OK;
    }

    @Override
    public byte[] getFileByName(String fileName) {
        log.info("Запрос файла {} пользователем {}", fileName, customUserDetailsService.getUsernameByContext());
        return filesRepository.getById(new FileInfoKey(fileName, customUserDetailsService.getUsernameByContext())).getFile();
    }

    @Override
    public HttpStatus deleteFile(String fileName) {
        log.info("Попытка удаления файла {} пользователем {}", fileName, customUserDetailsService.getUsernameByContext());
        filesRepository.deleteById(new FileInfoKey(fileName, customUserDetailsService.getUsernameByContext()));
        log.info("Файл успешно удален");
        return HttpStatus.OK;
    }
}
