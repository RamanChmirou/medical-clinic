package com.kanapa4.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class PatientDoesNotExistsException extends MedicalClinicException {
    public PatientDoesNotExistsException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
