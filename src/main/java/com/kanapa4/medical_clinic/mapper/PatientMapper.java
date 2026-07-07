package com.kanapa4.medical_clinic.mapper;

import com.kanapa4.medical_clinic.model.dto.PatientDto;
import com.kanapa4.medical_clinic.model.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientDto toDto(Patient patient);
}
