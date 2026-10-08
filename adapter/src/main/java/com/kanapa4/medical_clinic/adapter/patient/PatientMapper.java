package com.kanapa4.medical_clinic.adapter.patient;

import com.kanapa4.medical_clinic.adapter.PatientEntity;
import com.kanapa4.medical_clinic.model.Patient;
import com.kanapa4.medical_clinic.model.PatientDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    @Mapping(target = "userId", source = "user.id")
    Patient toPojo(PatientEntity entity);

    @Mapping(target = "user", ignore = true)
    PatientEntity toEntity(Patient patient);

    PatientDto toDto(Patient patient);
}
