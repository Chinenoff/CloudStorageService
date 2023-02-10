package com.example.cloudstorageservice.integrations;

import com.example.cloudstorageservice.model.db.FileInfoEntity;
import com.example.cloudstorageservice.model.db.FileInfoKey;
import com.example.cloudstorageservice.model.response.FileInfo;
import com.example.cloudstorageservice.repository.FilesRepository;
import com.example.cloudstorageservice.service.DataServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.List;

@SpringBootTest
@Testcontainers
public class DataIntegrationsTest {

    @Autowired
    private DataServiceImpl dataService;
    @Autowired
    private FilesRepository filesRepository;

    @Container
    public static PostgreSQLContainer<?> postgresSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("db")
            .withUsername("postgres")
            .withPassword("pass");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> String.format("jdbc:postgresql://localhost:%d/db", postgresSQLContainer.getFirstMappedPort()));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "pass");
        registry.add("spring.liquibase.enabled", () -> true);
    }


    @Test
    @Transactional
    @WithMockUser("user")
    public void uploadFileIntegrationSuccessCaseTest() throws IOException {
        //given
        final String filename = "filename";
        final String username = "user";
        final byte[] bytes = new byte[16];
        final MultipartFile file = new MockMultipartFile(filename, bytes);
        final FileInfoEntity expectedEntity = new FileInfoEntity(filename, username, file.getBytes(), file.getSize());
        //when
        dataService.uploadFile(filename, file);
        final FileInfoEntity actualEntity = filesRepository.getById(new FileInfoKey(filename, username));
        //then
        Assertions.assertEquals(expectedEntity, actualEntity);
    }

    @Test
    @Transactional
    @WithMockUser("user")
    public void getFileByNameSuccessIntegrationCase() {
        //given
        final String filename = "test.txt";
        final String username = "user";
        final byte[] frontFile = new byte[16];
        final long size = 16L;
        final FileInfoEntity entity = new FileInfoEntity(filename, username, frontFile, size);
        filesRepository.saveAndFlush(entity);
        //when
        final byte[] dbFile = dataService.getFileByName(filename);
        //then
        Assertions.assertArrayEquals(frontFile, dbFile);
    }

    @Test
    @Transactional
    @WithMockUser("user")
    public void deleteFileSuccessIntegrationCase() {
        //given
        final String filename = "test.txt";
        final String username = "user";
        final byte[] frontFile = new byte[16];
        final long size = 16L;
        final FileInfoEntity entity = new FileInfoEntity(filename, username, frontFile, size);
        filesRepository.saveAndFlush(entity);
        //when
        dataService.deleteFile(filename);
        JpaObjectRetrievalFailureException thrown = Assertions.assertThrows(JpaObjectRetrievalFailureException.class, () -> {
            filesRepository.getById(new FileInfoKey(filename, username));
        });
        //then
        final String expectedMessage = "Unable to find com.example.cloudstorageservice.model.db.FileInfoEntity with id FileInfoKey(fileName=test.txt, username=user)";
        Assertions.assertEquals(expectedMessage, thrown.getCause().getMessage());
    }

    @Test
    @Transactional
    @WithMockUser("user")
    public void editFileNameIntegrationSuccessCaseTest() {
        //given
        final String oldFilename = "test.txt";
        final String newFilename = "renamed.txt";
        final String username = "user";
        final byte[] frontFile = new byte[16];
        final long size = 16L;
        final FileInfoEntity basedEntity = new FileInfoEntity(oldFilename, username, frontFile, size);
        filesRepository.saveAndFlush(basedEntity);
        //when
        dataService.editFileName(oldFilename, newFilename);
        final FileInfoEntity renamedFileEntity = filesRepository.getById(new FileInfoKey(newFilename, username));
        //then
        Assertions.assertEquals(newFilename, renamedFileEntity.getFileName());
    }

    @Test
    @Transactional
    @WithMockUser("user")
    public void getFileNamesIntegrationSuccessCaseTest() {
        //given
        final String filename = "test.txt";
        final String username = "user";
        final byte[] frontFile = new byte[16];
        final long size = 16L;
        final FileInfoEntity entity = new FileInfoEntity(filename, username, frontFile, size);
        filesRepository.saveAndFlush(entity);
        final FileInfo expectedResult = new FileInfo(filename, size);
        //then
        final List<FileInfo> actualResultList = dataService.getFileNames();
        //when
        Assertions.assertEquals(expectedResult, actualResultList.get(0));
    }

}
