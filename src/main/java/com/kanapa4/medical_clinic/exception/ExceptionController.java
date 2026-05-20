package com.kanapa4.medical_clinic.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(InvalidPatientException.class)
    public ResponseEntity<ProblemDetail> handleInvalidPatientException(InvalidPatientException exception) {
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatusCode.valueOf(400), exception.getMessage());
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(PatientAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handlePatientAlreadyExistsException(InvalidPatientException exception) {
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatusCode.valueOf(409), exception.getMessage());
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(PatientDoesNotExistsException.class)
    public ResponseEntity<ProblemDetail> handlePatientDoesNotExistsException(InvalidPatientException exception) {
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatusCode.valueOf(404), exception.getMessage());
        return ResponseEntity.badRequest().body(problemDetail);
    }
}
