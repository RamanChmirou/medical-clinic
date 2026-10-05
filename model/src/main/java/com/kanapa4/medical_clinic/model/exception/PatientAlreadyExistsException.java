package com.kanapa4.medical_clinic.model.exception;

public class PatientAlreadyExistsException extends MedicalClinicException {
    public PatientAlreadyExistsException(String message) {
        super(message);
    }
}