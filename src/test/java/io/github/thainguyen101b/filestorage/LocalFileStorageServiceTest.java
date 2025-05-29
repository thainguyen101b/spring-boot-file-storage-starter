package io.github.thainguyen101b.filestorage;

import io.github.thainguyen101b.filestorage.config.FileStorageProperties;
import io.github.thainguyen101b.filestorage.exception.FileStorageException;
import io.github.thainguyen101b.filestorage.model.FileMetadata;
import io.github.thainguyen101b.filestorage.service.impl.LocalFileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalFileStorageServiceTest {

    @TempDir
    Path tempDir;

    private LocalFileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        FileStorageProperties properties = new FileStorageProperties();
        properties.setUploadDir(tempDir.toString());
        properties.setMaxFileSize(10485760L);
        properties.setAllowedExtensions(List.of("jpg", "jpeg", "png", "txt"));
        properties.setCreateDirectories(true);
        properties.setBaseUrl("/files");

        fileStorageService = new LocalFileStorageService(properties);
    }

    @Test
    void shouldStoreFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello World".getBytes()
        );

        FileMetadata metadata = fileStorageService.storeFile(file);

        assertNotNull(metadata.getId());
        assertEquals("test.txt", metadata.getOriginalName());
        assertEquals("text/plain", metadata.getContentType());
        assertEquals(11L, metadata.getSize());
    }

    @Test
    void shouldThrowExceptionForInvalidFileType() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.exe",
                "application/octet-stream",
                "Hello World".getBytes()
        );

        assertThrows(FileStorageException.class, () -> fileStorageService.storeFile(file));
    }
}