package io.github.thainguyen101b.filestorage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "file-storage")
public class FileStorageProperties {

    private final boolean enabled;
    private final String uploadDir;
    private final long maxFileSize;
    private final String[] allowedExtensions;
    private final boolean createDirectories;
    private final String baseUrl;

    @ConstructorBinding
    public FileStorageProperties(boolean enabled, String uploadDir, long maxFileSize, String[] allowedExtensions, boolean createDirectories, String baseUrl) {
        this.enabled = enabled;
        this.uploadDir = uploadDir != null ? uploadDir : "./uploads";
        this.maxFileSize = maxFileSize > 0 ? maxFileSize : 10485760;
        this.allowedExtensions = allowedExtensions != null ? allowedExtensions :
                new String[]{"jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "txt"};
        this.createDirectories = createDirectories;
        this.baseUrl = baseUrl != null ? baseUrl : "/api/files";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public String[] getAllowedExtensions() {
        return allowedExtensions;
    }

    public boolean isCreateDirectories() {
        return createDirectories;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
