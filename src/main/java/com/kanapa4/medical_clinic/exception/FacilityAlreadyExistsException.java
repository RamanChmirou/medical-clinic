package com.kanapa4.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class FacilityAlreadyExistsException extends MedicalClinicException {
    public FacilityAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
