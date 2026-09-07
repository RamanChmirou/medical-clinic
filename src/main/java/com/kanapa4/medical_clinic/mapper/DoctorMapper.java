package com.kanapa4.medical_clinic.mapper;

import com.kanapa4.medical_clinic.model.dto.DoctorDto;
import com.kanapa4.medical_clinic.model.entity.Doctor;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = { FacilityMapper.class },
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DoctorMapper {
    DoctorDto toDto(Doctor doctor);
}
