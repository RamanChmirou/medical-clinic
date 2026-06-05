package com.kanapa4.medical_clinic.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VisitDto {
    private Long id;
    private LocalDateTime dateTime;
    private Long doctorId;
    private Long patientId;
}
