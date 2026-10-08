package com.kanapa4.medical_clinic.model.exception;

public class UserAlreadyExistsException extends MedicalClinicException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
