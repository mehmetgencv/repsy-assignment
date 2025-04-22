package io.repsy.storage;

import io.minio.*;

import java.io.IOException;
import java.io.InputStream;

public class ObjectStorageService implements StorageService {
    private final MinioClient minioClient;
    private final String bucketName;

    public ObjectStorageService(MinioClient minioClient, String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        ensureBucketExists();
    }

    private void ensureBucketExists() {
        try {

            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
                System.out.println("Bucket '" + bucketName + "' created successfully.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create or verify bucket: " + e.getMessage());
        }
    }

    @Override
    public void saveFile(String packageName, String version, String fileName, InputStream fileContent) throws IOException {
        try {
            String objectKey = getObjectKey(packageName, version, fileName);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(fileContent, fileContent.available(), -1)
                            .contentType("application/octet-stream")
                            .build()
            );
        } catch (Exception e) {
            throw new IOException("Failed to save file to MinIO: " + e.getMessage(), e);
        }
    }

    @Override
    public InputStream getFile(String packageName, String version, String fileName) throws IOException {
        try {
            String objectKey = getObjectKey(packageName, version, fileName);
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            throw new IOException("Failed to retrieve file from MinIO: " + e.getMessage(), e);
        }
    }

    private String getObjectKey(String packageName, String version, String fileName) {
        return packageName + "/" + version + "/" + fileName;
    }
}