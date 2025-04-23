package io.repsy.repsyapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.repsy.repsyapi.dto.MetaJson;
import io.repsy.repsyapi.entity.PackageEntity;
import io.repsy.repsyapi.exception.PackageNotFoundException;
import io.repsy.repsyapi.exception.PackageValidationException;
import io.repsy.repsyapi.repository.PackageRepository;
import io.repsy.storage.StorageService;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class PackageService {

    private final StorageService storageService;
    private final ObjectMapper objectMapper;
    private final PackageRepository packageRepository;

    public PackageService(StorageService storageService, ObjectMapper objectMapper, PackageRepository packageRepository) {
        this.storageService = storageService;
        this.objectMapper = objectMapper;
        this.packageRepository = packageRepository;
    }

    @Transactional
    public void uploadPackage(String packageName, String version, MultipartFile repFile, MultipartFile metaFile) throws IOException {

        if (repFile == null || repFile.isEmpty()) {
            throw new PackageValidationException("repFile is required");
        }
        if (metaFile == null || metaFile.isEmpty()) {
            throw new PackageValidationException("metaFile is required");
        }
        String repFileName = repFile.getOriginalFilename();
        String metaFileName = metaFile.getOriginalFilename();

        if (repFileName == null || !repFileName.endsWith(".rep")) {
            throw new PackageValidationException("Invalid .rep file");
        }
        if (metaFileName == null || !metaFileName.endsWith(".json")) {
            throw new PackageValidationException("Invalid meta.json file");
        }

        try (ZipArchiveInputStream zipInput = new ZipArchiveInputStream(repFile.getInputStream())) {
            if (zipInput.getNextEntry() == null) {
                throw new PackageValidationException("Invalid zip format for .rep file");
            }
        }

        MetaJson metaJson;
        try {
            metaJson = objectMapper.readValue(metaFile.getInputStream(), MetaJson.class);
            if (!metaJson.getName().equals(packageName) || !metaJson.getVersion().equals(version)) {
                throw new PackageValidationException("meta.json name or version does not match");
            }
        } catch (Exception e) {
            throw new PackageValidationException("Invalid meta.json format");
        }

        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName(packageName);
        packageEntity.setVersion(version);
        packageEntity.setAuthor(metaJson.getAuthor());
        packageRepository.save(packageEntity);


        storageService.saveFile(packageName, version, "package.rep", repFile.getInputStream());
        storageService.saveFile(packageName, version, "meta.json", metaFile.getInputStream());
    }

    public InputStream downloadPackage(String packageName, String version, String fileName) throws IOException {
        if (!fileName.equals("package.rep") && !fileName.equals("meta.json")) {
            throw new PackageValidationException("Invalid file name");
        }
        InputStream fileStream = storageService.getFile(packageName, version, fileName);
        if (fileStream == null) {
            throw new PackageNotFoundException("File not found: " + fileName);
        }
        return fileStream;
    }
}