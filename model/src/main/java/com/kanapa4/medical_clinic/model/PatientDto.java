package com.kanapa4.medical_clinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
