package com.kanapa4.medical_clinic.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientCreatedDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Long idCardNumber;
    private LocalDate birthday;
    private String phoneNumber;
}