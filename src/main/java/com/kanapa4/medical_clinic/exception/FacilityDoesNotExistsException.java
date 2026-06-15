package com.kanapa4.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class FacilityDoesNotExistsException extends MedicalClinicException {
    public FacilityDoesNotExistsException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
