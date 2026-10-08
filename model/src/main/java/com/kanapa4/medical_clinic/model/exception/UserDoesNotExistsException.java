package com.kanapa4.medical_clinic.model.exception;

public class UserDoesNotExistsException extends MedicalClinicException {
    public UserDoesNotExistsException(String message) {
        super(message);
    }
}
