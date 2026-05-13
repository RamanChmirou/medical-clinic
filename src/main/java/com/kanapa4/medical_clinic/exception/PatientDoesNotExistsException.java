package com.kanapa4.medical_clinic.exception;

public class PatientDoesNotExistsException extends RuntimeException {
    public PatientDoesNotExistsException(String message) {
        super(message);
    }
}
