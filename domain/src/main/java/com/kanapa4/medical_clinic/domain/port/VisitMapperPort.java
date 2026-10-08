package com.kanapa4.medical_clinic.domain.port;

import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Visit;
import com.kanapa4.medical_clinic.model.VisitDto;

public interface VisitMapperPort {
    VisitDto toDto(Visit visit);

    Page<VisitDto> toPageDto(Page<Visit> page);
}