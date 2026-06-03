package com.kanapa4.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class DoctorDoesNotExistsException extends MedicalClinicException {
    public DoctorDoesNotExistsException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
