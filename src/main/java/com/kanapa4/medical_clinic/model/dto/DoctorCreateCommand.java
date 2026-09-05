package com.kanapa4.medical_clinic.model.dto;

import com.kanapa4.medical_clinic.model.Specialization;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DoctorCreateCommand {
    private String firstName;
    private String lastName;
    private Specialization specialization;
    private Long userId;
}
