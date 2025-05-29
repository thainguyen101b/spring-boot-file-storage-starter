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

import static org.junit.jupiter.api.Assertions.*;

class LocalFileStorageServiceTest {

    @TempDir
    Path tempDir;

    private LocalFileStorageService fileStorageService;
    private FileStorageProperties properties;

    @BeforeEach
    void setUp() {
        properties = new FileStorageProperties(
                true,
                tempDir.toString(),
                10485760L,
                new String[]{"jpg", "jpeg", "png", "txt"},
                true,
                "/api/files"
        );
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