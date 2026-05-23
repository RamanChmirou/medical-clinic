package com.kanapa4.medical_clinic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(MedicalClinicException.class)
    public ResponseEntity<ProblemDetail> handleMedicalClinicException(MedicalClinicException exception) {
        HttpStatus status = exception.getHttpStatus();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(status.value()),
                exception.getMessage()
        );

        return ResponseEntity.status(status).body(problemDetail);
    }
}
