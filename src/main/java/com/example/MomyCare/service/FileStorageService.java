package com.example.MomyCare.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String UPLOAD_DIR = "uploads/";

    public String saveFile(MultipartFile file) {

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path path = Paths.get(UPLOAD_DIR + fileName);

            Files.createDirectories(path.getParent());

            Files.copy(file.getInputStream(), path);

            return path.toString();

        } catch (Exception e) {
            throw new RuntimeException("Erreur upload fichier");
        }
    }

    public void deleteFile(String path) {
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (Exception ignored) {}
    }
}
