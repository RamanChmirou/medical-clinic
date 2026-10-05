package com.kanapa4.medical_clinic.model;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private Specialization specialization;
    @Builder.Default
    private Set<Long> facilityIds = new HashSet<>();

    public void addFacility(Long facilityId) {
        facilityIds.add(facilityId);
    }

    public void removeFacility(Long facilityId) {
        facilityIds.remove(facilityId);
    }

    public static Doctor create(DoctorCreateCommand dto) {
        return Doctor.builder()
                .userId(dto.getUserId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .specialization(dto.getSpecialization())
                .build();
    }
}
