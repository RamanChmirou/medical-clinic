package com.kanapa4.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class InvalidPatientException extends MedicalClinicException {
    public InvalidPatientException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
