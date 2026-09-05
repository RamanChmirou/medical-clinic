package com.kanapa4.medical_clinic.model.dto;

import com.kanapa4.medical_clinic.model.Specialization;
import com.kanapa4.medical_clinic.model.entity.Facility;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class DoctorDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Specialization specialization;
    private Set<FacilityDto> facilities;
}
