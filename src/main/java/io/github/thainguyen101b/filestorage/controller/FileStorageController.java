package io.github.thainguyen101b.filestorage.controller;

import io.github.thainguyen101b.filestorage.model.FileMetadata;
import io.github.thainguyen101b.filestorage.model.FileUploadResponse;
import io.github.thainguyen101b.filestorage.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("${file-storage.base-url:/files}")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                                        @RequestParam(value = "path", required = false) String customPath) {
        FileMetadata metadata = fileStorageService.storeFile(file, customPath);

        String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/download/")
                .path(metadata.getId())
                .toUriString();

        FileUploadResponse response = new FileUploadResponse(
                metadata.getId(),
                metadata.getOriginalName(),
                downloadUri,
                metadata.getSize(),
                metadata.getContentType()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload/multiple")
    public ResponseEntity<List<FileUploadResponse>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
                                                                       @RequestParam(value = "path", required = false) String customPath) {
        List<FileUploadResponse> responses = Arrays.stream(files)
                .map(file -> {
                    FileMetadata metadata = fileStorageService.storeFile(file, customPath);
                    String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/files/download/")
                            .path(metadata.getId())
                            .toUriString();

                    return new FileUploadResponse(
                            metadata.getId(),
                            metadata.getOriginalName(),
                            downloadUri,
                            metadata.getSize(),
                            metadata.getContentType()
                    );
                })
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(fileId);
        FileMetadata metadata = fileStorageService.getFileMetadata(fileId);

        String contentType;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (Exception ex) {
            // Fallback to metadata content type
            contentType = metadata.getContentType();
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getOriginalName() + "\"")
                .body(resource);
    }

    @GetMapping("/metadata/{fileId}")
    public ResponseEntity<FileMetadata> getFileMetadata(@PathVariable String fileId) {
        FileMetadata metadata = fileStorageService.getFileMetadata(fileId);
        return ResponseEntity.ok(metadata);
    }

    @GetMapping
    public ResponseEntity<List<FileMetadata>> getAllFiles() {
        List<FileMetadata> files = fileStorageService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileId) {
        boolean deleted = fileStorageService.deleteFile(fileId);
        if (deleted) {
            return ResponseEntity.ok("File deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllFiles() {
        fileStorageService.deleteAllFiles();
        return ResponseEntity.ok("All files deleted successfully");
    }
}
