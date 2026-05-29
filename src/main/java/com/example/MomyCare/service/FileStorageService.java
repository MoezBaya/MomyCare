package com.example.MomyCare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * Service de stockage de fichiers sécurisé.
 *
 * Corrections apportées :
 *  - Whitelist des extensions et types MIME autorisés
 *  - Limite de taille configurable (défaut 10 Mo)
 *  - Nom de fichier aléatoire (UUID) → pas de path traversal
 *  - Répertoire de destination configurable via application.properties
 *  - Vérification que le fichier résolu reste bien dans le répertoire cible
 */
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

    /** Taille maximale : 10 Mo */
    private static final long MAX_SIZE_BYTES = 10L * 1024 * 1024;

    /** Répertoire cible, configurable dans application.properties */
    @Value("${app.storage.upload-dir:uploads}")
    private String uploadDir;

    // ─── Upload ───────────────────────────────────────────────────────────────

    /**
     * Valide et stocke le fichier.
     *
     * @return chemin relatif du fichier sauvegardé (à persister en base)
     */
    public String saveFile(MultipartFile file) {

        validateFile(file);

        String safeFileName = buildSafeFileName(file.getOriginalFilename());

        Path targetDir  = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path targetPath = targetDir.resolve(safeFileName).normalize();

        // Protection anti path-traversal : le fichier résolu doit rester dans targetDir
        if (!targetPath.startsWith(targetDir)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Nom de fichier invalide");
        }

        try {
            Files.createDirectories(targetDir);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de l'enregistrement du fichier");
        }

        return safeFileName;
    }

    // ─── Suppression ──────────────────────────────────────────────────────────

    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isBlank()) return;

        Path targetDir  = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path targetPath = targetDir.resolve(fileName).normalize();

        // Ne jamais supprimer en dehors du répertoire autorisé
        if (!targetPath.startsWith(targetDir)) return;

        try {
            Files.deleteIfExists(targetPath);
        } catch (IOException ignored) {
            // On logge en prod mais on ne fait pas remonter l'erreur
        }
    }

    // ─── Helpers privés ───────────────────────────────────────────────────────

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Le fichier est vide ou absent");
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "Fichier trop volumineux (maximum 10 Mo)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Type de fichier non autorisé. Types acceptés : JPEG, PNG, WEBP, PDF");
        }

        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Extension de fichier non autorisée");
        }
    }

    /**
     * Génère un nom de fichier aléatoire tout en conservant l'extension originale.
     * Le nom original n'est JAMAIS utilisé dans le chemin de stockage.
     */
    private String buildSafeFileName(String originalFilename) {
        String extension = getExtension(originalFilename);
        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Nom de fichier invalide ou sans extension");
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}