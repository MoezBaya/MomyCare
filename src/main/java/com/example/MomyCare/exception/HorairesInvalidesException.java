package com.example.MomyCare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public class HorairesInvalidesException extends RuntimeException {

        public HorairesInvalidesException(String heureDebut, String heureFin) {
            super("L'heure de fin (%s) doit être strictement après l'heure de début (%s)."
                    .formatted(heureFin, heureDebut));
        }
    }

