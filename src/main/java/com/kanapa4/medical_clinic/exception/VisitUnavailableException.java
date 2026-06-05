package com.kanapa4.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class VisitUnavailableException extends MedicalClinicException {
    public VisitUnavailableException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
