package com.kanapa4.medical_clinic.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PatientDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
