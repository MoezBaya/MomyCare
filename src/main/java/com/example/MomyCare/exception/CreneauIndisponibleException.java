package com.example.MomyCare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ResponseStatus(HttpStatus.CONFLICT)
public class CreneauIndisponibleException extends RuntimeException {

    public CreneauIndisponibleException(LocalDateTime creneau) {
        super("Le créneau %s est déjà réservé ou n'existe pas dans les disponibilités du médecin."
                .formatted(creneau));
    }

    public CreneauIndisponibleException(String message) {
        super(message);
    }
}