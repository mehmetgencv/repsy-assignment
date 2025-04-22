package io.repsy.repsyapi.config;

import io.minio.MinioClient;
import io.repsy.storage.FileSystemStorageService;
import io.repsy.storage.ObjectStorageService;
import io.repsy.storage.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {


    @Value("${storageStrategy:file-system}")
    private String storageStrategy;

    @Value("${file.storage.path:/storage}")
    private String basePath;

    @Value("${minio.url:http://minio:9000}")
    private String minioUrl;

    @Value("${minio.accessKey:minioadmin}")
    private String minioAccessKey;

    @Value("${minio.secretKey:minioadmin}")
    private String minioSecretKey;

    @Value("${minio.bucket:repsy}")
    private String minioBucketName;

    @Bean
    public StorageService storageService(FileSystemStorageService fileSystemStorageService,
                                         ObjectStorageService objectStorageService) {
        return "file-system".equalsIgnoreCase(storageStrategy)
                ? fileSystemStorageService
                : objectStorageService;
    }

    @Bean
    public FileSystemStorageService fileSystemStorageService() {
        return new FileSystemStorageService(basePath);
    }

    @Bean
    public ObjectStorageService objectStorageService(MinioClient minioClient) {
        return new ObjectStorageService(minioClient, minioBucketName);
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(minioAccessKey, minioSecretKey)
                .build();
    }
}