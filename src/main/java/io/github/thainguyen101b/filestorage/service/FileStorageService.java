package io.github.thainguyen101b.filestorage.service;

import io.github.thainguyen101b.filestorage.model.FileMetadata;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    FileMetadata storeFile(MultipartFile file);

    FileMetadata storeFile(MultipartFile file, String customPath);

    Resource loadFileAsResource(String fileId);

    FileMetadata getFileMetadata(String fileId);

    List<FileMetadata> getAllFiles();

    boolean deleteFile(String fileId);

    void deleteAllFiles();
}