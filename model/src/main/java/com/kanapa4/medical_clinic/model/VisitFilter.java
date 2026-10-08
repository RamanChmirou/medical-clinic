package com.kanapa4.medical_clinic.model;

import java.time.LocalDate;

public record VisitFilter(
        Long patientId,
        Long doctorId,
        Specialization specialization,
        LocalDate date,
        LocalDate startDate,
        LocalDate endDate,
        Boolean available
) {}
