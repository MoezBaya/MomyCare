package com.example.MomyCare.service.impl;

import com.example.MomyCare.exception.BadRequestException;
import com.example.MomyCare.exception.FileStorageException;
import com.example.MomyCare.exception.UnsupportedMediaTypeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "application/pdf"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "webp", "pdf"
    );

    private static final long MAX_SIZE_BYTES = 10L * 1024 * 1024;

    @Value("${app.storage.upload-dir:uploads}")
    private String uploadDir;

    public String saveFile(MultipartFile file) {
        validateFile(file);

        String safeFileName = buildSafeFileName(file.getOriginalFilename());

        Path targetDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path targetPath = targetDir.resolve(safeFileName).normalize();

        if (!targetPath.startsWith(targetDir)) {
            throw new BadRequestException("Nom de fichier invalide");
        }

        try {
            Files.createDirectories(targetDir);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new FileStorageException("Erreur lors de l'enregistrement du fichier", e);
        }

        return safeFileName;
    }

    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isBlank()) return;

        Path targetDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path targetPath = targetDir.resolve(fileName).normalize();

        if (!targetPath.startsWith(targetDir)) return;

        try {
            Files.deleteIfExists(targetPath);
        } catch (IOException ignored) {
            // log silently
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Le fichier est vide ou absent");
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new BadRequestException("Fichier trop volumineux (maximum 10 Mo)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new UnsupportedMediaTypeException(
                    "Type de fichier non autorisé. Types acceptés : JPEG, PNG, WEBP, PDF");
        }

        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new UnsupportedMediaTypeException("Extension de fichier non autorisée");
        }
    }

    private String buildSafeFileName(String originalFilename) {
        String extension = getExtension(originalFilename);
        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new BadRequestException("Nom de fichier invalide ou sans extension");
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}