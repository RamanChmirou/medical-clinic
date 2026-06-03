package com.kanapa4.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class DoctorAlreadyExistsException extends MedicalClinicException {
    public DoctorAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
