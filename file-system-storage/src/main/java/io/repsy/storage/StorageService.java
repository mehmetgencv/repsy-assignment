package io.repsy.storage;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {
    void saveFile(String packageName, String version, String fileName, InputStream fileContent) throws IOException;
    InputStream getFile(String packageName, String version, String fileName) throws IOException;
}