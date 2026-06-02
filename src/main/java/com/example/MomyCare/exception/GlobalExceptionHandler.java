package com.example.MomyCare.exception;

import com.example.MomyCare.dto.error.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------- Exceptions métier "disponibilité" ----------
    @ExceptionHandler(DisponibiliteOverlapException.class)
    public ResponseEntity<ErrorResponse> handleOverlap(DisponibiliteOverlapException ex) {
        return buildError(HttpStatus.CONFLICT, "OVERLAP", ex.getMessage());
    }

    @ExceptionHandler(DuplicateDisponibiliteException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateDisponibiliteException ex) {
        return buildError(HttpStatus.CONFLICT, "DUPLICATE", ex.getMessage());
    }

    @ExceptionHandler(InvalidTimeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTime(InvalidTimeException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "INVALID_TIME", ex.getMessage());
    }

    // ---------- Erreurs de validation Spring ----------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("Validation échouée");
        return buildError(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", msg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "INVALID_FORMAT",
                "Format de données incorrect. Vérifiez les dates (YYYY-MM-DD) et heures (HH:MM).");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return buildError(HttpStatus.CONFLICT, "DATA_INTEGRITY_VIOLATION",
                "Conflit avec des données existantes (doublon ou référence invalide).");
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "INVALID_STATE", ex.getMessage());
    }

    // ---------- Exceptions générales ----------
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        return buildError(HttpStatus.FORBIDDEN, "FORBIDDEN", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ex.printStackTrace();
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "Une erreur inattendue s'est produite.");
    }

    // ---------- Méthode utilitaire ----------
    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String code, String message) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                code,
                message
        );
        return new ResponseEntity<>(error, status);
    }
}