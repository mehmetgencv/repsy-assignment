package io.repsy.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileSystemStorageService implements StorageService {
    private final String basePath;

    public FileSystemStorageService(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public void saveFile(String packageName, String version, String fileName, InputStream fileContent) throws IOException {
        Path filePath = Paths.get(basePath, packageName, version, fileName);
        Files.createDirectories(filePath.getParent());
        Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public InputStream getFile(String packageName, String version, String fileName) throws IOException {
        Path filePath = Paths.get(basePath, packageName, version, fileName);
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + filePath);
        }
        return Files.newInputStream(filePath);
    }
}