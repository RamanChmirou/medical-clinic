package com.kanapa4.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class VisitAlreadyExistsException extends MedicalClinicException {
    public VisitAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
