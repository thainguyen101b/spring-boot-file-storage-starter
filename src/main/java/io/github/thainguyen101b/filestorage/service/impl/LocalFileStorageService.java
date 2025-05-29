package io.github.thainguyen101b.filestorage.service.impl;

import io.github.thainguyen101b.filestorage.config.FileStorageProperties;
import io.github.thainguyen101b.filestorage.exception.FileNotFoundException;
import io.github.thainguyen101b.filestorage.exception.FileStorageException;
import io.github.thainguyen101b.filestorage.model.FileMetadata;
import io.github.thainguyen101b.filestorage.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path fileStorageLocation;
    private final FileStorageProperties properties;
    private final Map<String, FileMetadata> fileMetadataStore = new ConcurrentHashMap<>();

    public LocalFileStorageService(FileStorageProperties properties) {
        this.properties = properties;
        this.fileStorageLocation = Paths.get(properties.getUploadDir()).toAbsolutePath().normalize();

        try {
            if (properties.isCreateDirectories()) {
                Files.createDirectories(this.fileStorageLocation);
            }
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public FileMetadata storeFile(MultipartFile file) {
        return storeFile(file, null);
    }

    @Override
    public FileMetadata storeFile(MultipartFile file, String customPath) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            validateFile(file, fileName);

            // Generate unique file ID and stored name
            String fileId = UUID.randomUUID().toString();
            String fileExtension = getFileExtension(fileName);
            String storedName = fileId + (fileExtension.isEmpty() ? "" : "." + fileExtension);

            // Determine the storage path
            Path targetLocation;
            if (customPath != null && !customPath.trim().isEmpty()) {
                Path customDir = this.fileStorageLocation.resolve(Paths.get(customPath)).normalize();
                Files.createDirectories(customDir);
                targetLocation = customDir.resolve(storedName);
            } else {
                targetLocation = this.fileStorageLocation.resolve(storedName);
            }

            // Copy file to target location
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Create and store metadata
            FileMetadata metadata = new FileMetadata(
                fileId,
                fileName,
                storedName,
                file.getContentType(),
                file.getSize(),
                targetLocation.toString()
            );

            fileMetadataStore.put(fileId, metadata);
            return metadata;

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileId) {
        try {
            FileMetadata metadata = getFileMetadata(fileId);
            Path filePath = Paths.get(metadata.getPath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileId);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileId, ex);
        }
    }

    @Override
    public FileMetadata getFileMetadata(String fileId) {
        FileMetadata metadata = fileMetadataStore.get(fileId);
        if (metadata == null) {
            throw new FileNotFoundException("File not found with id " + fileId);
        }
        return metadata;
    }

    @Override
    public List<FileMetadata> getAllFiles() {
        return new ArrayList<>(fileMetadataStore.values());
    }

    @Override
    public boolean deleteFile(String fileId) {
        try {
            FileMetadata metadata = getFileMetadata(fileId);
            Path filePath = Paths.get(metadata.getPath());
            
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                fileMetadataStore.remove(fileId);
            }
            return deleted;
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + fileId, ex);
        }
    }

    @Override
    public void deleteAllFiles() {
        for (String fileId : new HashSet<>(fileMetadataStore.keySet())) {
            deleteFile(fileId);
        }
    }

    private void validateFile(MultipartFile file, String fileName) {
        if (fileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
        }

        if (file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file " + fileName);
        }

        if (file.getSize() > properties.getMaxFileSize()) {
            throw new FileStorageException("File size exceeds maximum limit of " + properties.getMaxFileSize() + " bytes");
        }

        String fileExtension = getFileExtension(fileName).toLowerCase();
        if (!isAllowedExtension(fileExtension)) {
            throw new FileStorageException("File type not allowed: " + fileExtension);
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }

    private boolean isAllowedExtension(String extension) {
        return properties.getAllowedExtensions().stream()
                .anyMatch(allowed -> allowed.equalsIgnoreCase(extension));
    }
}
