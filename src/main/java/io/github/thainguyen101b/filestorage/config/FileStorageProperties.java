package io.github.thainguyen101b.filestorage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

@ConfigurationProperties(prefix = "file-storage")
public class FileStorageProperties {

    private boolean enabled = true;
    private String uploadDir = "./uploads";
    private long maxFileSize = 10485760;
    private List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "txt");
    private boolean createDirectories = true;
    private String baseUrl = "/files";

    public boolean isEnabled() {
        return enabled;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    public boolean isCreateDirectories() {
        return createDirectories;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public void setAllowedExtensions(List<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public void setCreateDirectories(boolean createDirectories) {
        this.createDirectories = createDirectories;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
