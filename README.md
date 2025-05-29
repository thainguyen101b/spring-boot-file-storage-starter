# Spring Boot File Storage Starter

A simple and extensible Spring Boot starter for file storage operations.

## Features

- Local file system storage (AWS S3 support coming soon)
- Autoconfiguration with Spring Boot
- File validation (size, type, security)
- File metadata management
- Ready-to-use REST endpoints
- Highly configurable
- Well tested

## Quick Start

### 1. Add Dependency

```xml

<dependency>
    <groupId>io.github.thainguyen101b</groupId>
    <artifactId>spring-boot-file-storage-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Configuration (Optional)

```yaml
file-storage:
  enabled: true
  upload-dir: ./uploads
  max-file-size: 10485760 # 10MB
  allowed-extensions: jpg,jpeg,png,gif,pdf,doc,docx,txt
  create-directories: true
  base-url: /api/files
```

### 3. Use the APIs

That's it! The following endpoints are automatically available:

- `POST /api/files/upload` - Upload single file
- `POST /api/files/upload/multiple` - Upload multiple files
- `GET /api/files/download/{fileId}` - Download file
- `GET /api/files/metadata/{fileId}` - Get file metadata
- `GET /api/files` - List all files
- `DELETE /api/files/{fileId}` - Delete file
- `DELETE /api/files` - Delete all files

## Configuration Properties

| Property                          | Default                             | Description                       |
|-----------------------------------|-------------------------------------|-----------------------------------|
| `file-storage.enabled`            | `true`                              | Enable/disable the file storage   |
| `file-storage.upload-dir`         | `./uploads`                         | Directory to store files          |
| `file-storage.max-file-size`      | `10485760`                          | Maximum file size in bytes (10MB) |
| `file-storage.allowed-extensions` | `jpg,jpeg,png,gif,pdf,doc,docx,txt` | Allowed file extensions           |
| `file-storage.create-directories` | `true`                              | Auto-create upload directories    |
| `file-storage.base-url`           | `/api/files`                        | Base URL for endpoints            |


## Advanced Usage

### 1. Programmatic Usage

```java
@RestController
public class MyController {
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping("/my-upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        FileMetadata metadata = fileStorageService.storeFile(file);
        return ResponseEntity.ok(metadata);
    }
}
```
### 2. Custom Storage Path

```java
// Store file in custom subdirectory
FileMetadata metadata = fileStorageService.storeFile(file, "documents/2024");
```

### 3. Disable Auto-endpoints

```yaml
file-storage:
  enabled: false  # This will disable auto-configuration
```

Then create your own controller using the FileStorageService.

## Error Handling
The library throws these exceptions:

- `FileStorageException` - General storage errors
- `FileNotFoundException` - File not found errors

## Extending for Cloud Storage

The library is designed for extensibility. To add AWS S3 support:

```java
@Service
@ConditionalOnProperty(name = "file-storage.provider", havingValue = "s3")
public class S3FileStorageService implements FileStorageService {
    // Implementation for S3
}
```