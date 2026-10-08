package com.kanapa4.medical_clinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientCreateCommand {
    private String firstName;
    private String lastName;
    private String idCardNo;
    private LocalDate birthday;
    private String phoneNumber;
    private Long userId;
}
