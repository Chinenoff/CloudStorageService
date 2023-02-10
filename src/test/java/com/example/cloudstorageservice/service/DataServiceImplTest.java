package com.example.cloudstorageservice.service;

import com.example.cloudstorageservice.model.db.FileInfoEntity;
import com.example.cloudstorageservice.model.db.FileInfoKey;
import com.example.cloudstorageservice.model.response.FileInfo;
import com.example.cloudstorageservice.repository.FilesRepository;
import com.example.cloudstorageservice.repository.SecurityRepository;
import com.example.cloudstorageservice.security.CustomUserDetailsService;
import com.example.cloudstorageservice.service.DataServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DataServiceImpl.class, CustomUserDetailsService.class})
public class DataServiceImplTest {

    @Autowired
    private DataServiceImpl dataService;

    @MockBean
    private FilesRepository filesRepository;

    @MockBean
    private SecurityRepository securityRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    @Test
    @WithMockUser("user")
    public void editFileNameSuccessCaseTest() {
        //given
        final String oldFileName = "old";
        final String newName = "new";
        final String username = "user";
        final HttpStatus expectedStatus = HttpStatus.OK;
        //when
        HttpStatus actualStatus = dataService.editFileName(oldFileName, newName);
        //then
        Mockito.verify(filesRepository, Mockito.times(1)).editFileName(username, oldFileName, newName);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @WithMockUser("user")
    public void getFileNamesSuccessTest() {
        //given
        final List<FileInfo> expectedList = new ArrayList<>();
        expectedList.add(new FileInfo("name", 1L));
        final String username = "user";
        Mockito.when(filesRepository.findByName(username)).thenReturn(expectedList);
        //when
        final List<FileInfo> actualList = dataService.getFileNames();
        //then
        Assertions.assertEquals(expectedList.get(0), actualList.get(0));
    }

    @Test
    @WithMockUser("user")
    public void uploadFileSuccessTest() throws IOException {
        //given
        final String filename = "filename";
        final byte[] bytes = new byte[0];
        final MultipartFile file = new MockMultipartFile("name", bytes);
        final HttpStatus expectedStatus = HttpStatus.OK;
        //when
        final HttpStatus actualStatus = dataService.uploadFile(filename, file);
        //then
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @WithMockUser("user")
    public void deleteFileSuccessCaseTest() {
        //given
        final String username = "user";
        final HttpStatus expectedStatus = HttpStatus.OK;
        final String filename = "name";
        //when
        HttpStatus actualStatus = dataService.deleteFile(filename);
        //then
        Mockito.verify(filesRepository, Mockito.times(1)).deleteById(new FileInfoKey(filename, username));
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @WithMockUser("user")
    public void getFileByNameSuccessCaseTest() {
        //given
        final String filename = "name";
        final String username = "user";
        final byte[] expectedBytes = new byte[0];
        Mockito.when(filesRepository.getById(new FileInfoKey(filename, username)))
                .thenReturn(new FileInfoEntity(filename, username, expectedBytes, 1L));
        //when
        final byte[] actualBytes = dataService.getFileByName(filename);
        //then
        Assertions.assertEquals(expectedBytes, actualBytes);
    }

}
