package com.kanapa4.medical_clinic.model.exception;

public class PatientDoesNotExistsException extends MedicalClinicException {
    public PatientDoesNotExistsException(String message) {
        super(message);
    }
}
