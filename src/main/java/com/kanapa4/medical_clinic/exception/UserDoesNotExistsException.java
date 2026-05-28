package com.kanapa4.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class UserDoesNotExistsException extends MedicalClinicException {
    public UserDoesNotExistsException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
