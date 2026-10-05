package com.kanapa4.medical_clinic.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    private Long id;
    private String idCardNo;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;
    private Long userId;

    public static Patient create(PatientCreateCommand dto, Long userId) {
        return Patient.builder()
                .idCardNo(dto.getIdCardNo())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .birthday(dto.getBirthday())
                .userId(userId)
                .build();
    }
}
