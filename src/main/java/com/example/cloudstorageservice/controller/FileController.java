package com.example.cloudstorageservice.controller;

import com.example.cloudstorageservice.model.response.FileInfo;
import com.example.cloudstorageservice.service.DataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@CrossOrigin(
        origins = {"http://localhost:8080"},
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
@Validated
public class FileController {
    private final DataService dataService;

    @GetMapping("/list")
    public List<FileInfo> getFilenameList(@NotNull Integer limit) {
        return dataService.getFileNames();
    }

    @PostMapping("/file")
    public ResponseEntity<HttpStatus> putFile(@NotBlank String filename,
                                              MultipartFile file) throws IOException {
        return new ResponseEntity<>(dataService.uploadFile(filename, file));
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> getFileByFileName(@NotBlank String filename) {
        return new ResponseEntity<>(dataService.getFileByName(filename), HttpStatus.OK);
    }

    @DeleteMapping("/file")
    public ResponseEntity<HttpStatus> deleteFileByFileName(@NotBlank String filename) {
        return new ResponseEntity<>(dataService.deleteFile(filename));
    }

    @PutMapping("/file")
    public ResponseEntity<HttpStatus> editFileName(@RequestParam(name = "filename") @NotBlank String currentFileName,
                                                   @RequestBody() Map<String, String> body) {
        final String newFilenameHeader = "filename";
        return new ResponseEntity<>(dataService.editFileName(currentFileName, body.get(newFilenameHeader)));
    }
}
