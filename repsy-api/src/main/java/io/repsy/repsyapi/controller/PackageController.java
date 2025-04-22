package io.repsy.repsyapi.controller;

import io.repsy.repsyapi.service.PackageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/v1")
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @PostMapping("/{packageName}/{version}")
    public ResponseEntity<String> uploadPackage(
            @PathVariable String packageName,
            @PathVariable String version,
            @RequestParam("repFile") MultipartFile repFile,
            @RequestParam("metaFile") MultipartFile metaFile) {
        try {
            packageService.uploadPackage(packageName, version, repFile, metaFile);
            return ResponseEntity.ok("Package uploaded successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload package: " + e.getMessage());
        }
    }

    @GetMapping("/{packageName}/{version}/{fileName}")
    public ResponseEntity<InputStreamResource> downloadPackage(
            @PathVariable String packageName,
            @PathVariable String version,
            @PathVariable String fileName) {
        try {
            InputStream fileStream = packageService.downloadPackage(packageName, version, fileName);
            return ResponseEntity.ok()
                    .contentType(fileName.endsWith(".json") ? MediaType.APPLICATION_JSON : MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(new InputStreamResource(fileStream));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}