package com.kanapa4.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class PatientAlreadyExistsException extends MedicalClinicException {
    public PatientAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}