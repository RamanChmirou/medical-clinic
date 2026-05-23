package com.kanapa4.medical_clinic.model.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    public void update(Patient patientData) {
        this.firstName = patientData.getFirstName();
        this.lastName = lastName;
    }
}