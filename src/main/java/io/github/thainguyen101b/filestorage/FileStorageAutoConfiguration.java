package io.github.thainguyen101b.filestorage;

import io.github.thainguyen101b.filestorage.config.FileStorageProperties;
import io.github.thainguyen101b.filestorage.controller.FileStorageController;
import io.github.thainguyen101b.filestorage.service.FileStorageService;
import io.github.thainguyen101b.filestorage.service.impl.LocalFileStorageService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(FileStorageProperties.class)
@ConditionalOnProperty(prefix = "file-storage", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FileStorageAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FileStorageService fileStorageService(FileStorageProperties properties) {
        return new LocalFileStorageService(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public FileStorageController fileStorageController(FileStorageService fileStorageService) {
        return new FileStorageController(fileStorageService);
    }

}
