package com.kanapa4.medical_clinic.adapter;

import com.kanapa4.medical_clinic.model.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MedicalClinicException.class)
    public ResponseEntity<ProblemDetail> handleService1Exception(MedicalClinicException ex) {
        HttpStatus status = switch (ex) {
            case UserAlreadyExistsException e -> HttpStatus.CONFLICT;
            case UserDoesNotExistsException e -> HttpStatus.NOT_FOUND;
            case PatientDoesNotExistsException e -> HttpStatus.NOT_FOUND;
            case PatientAlreadyExistsException e -> HttpStatus.CONFLICT;
            case FacilityAlreadyExistsException e -> HttpStatus.CONFLICT;
            case FacilityDoesNotExistsException e -> HttpStatus.NOT_FOUND;
            case DoctorAlreadyExistsException e -> HttpStatus.CONFLICT;
            case DoctorDoesNotExistsException e -> HttpStatus.NOT_FOUND;
            case VisitUnavailableException e -> HttpStatus.CONFLICT;
            case VisitDoesNotExistsException e -> HttpStatus.NOT_FOUND;
            case InvalidVisitException e -> HttpStatus.BAD_REQUEST;
            case VisitAlreadyExistsException e -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        return ResponseEntity.status(status).body(problemDetail);
    }
}
