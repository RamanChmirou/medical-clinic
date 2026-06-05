package com.kanapa4.medical_clinic.mapper;

import com.kanapa4.medical_clinic.model.dto.VisitDto;
import com.kanapa4.medical_clinic.model.entity.Visit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VisitMapper {
    VisitDto toDto(Visit visit);
}
