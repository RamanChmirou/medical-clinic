package com.kanapa4.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class VisitDoesNotExistsException extends MedicalClinicException {
    public VisitDoesNotExistsException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
