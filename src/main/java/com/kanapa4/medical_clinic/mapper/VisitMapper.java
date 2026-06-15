package com.kanapa4.medical_clinic.mapper;

import com.kanapa4.medical_clinic.model.dto.VisitDto;
import com.kanapa4.medical_clinic.model.entity.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitMapper {

    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "patientId", source = "patient.id")
    VisitDto toDto(Visit visit);
}
