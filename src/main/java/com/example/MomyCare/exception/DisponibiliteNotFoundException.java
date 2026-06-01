package com.example.MomyCare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DisponibiliteNotFoundException extends RuntimeException {

    public DisponibiliteNotFoundException(Long id) {
        super("Disponibilité introuvable avec l'identifiant : " + id);
    }

    public DisponibiliteNotFoundException(String message) {
        super(message);
    }
}
