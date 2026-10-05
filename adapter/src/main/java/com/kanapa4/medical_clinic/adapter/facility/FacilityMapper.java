package com.kanapa4.medical_clinic.adapter.facility;

import com.kanapa4.medical_clinic.adapter.DoctorEntity;
import com.kanapa4.medical_clinic.adapter.FacilityEntity;
import com.kanapa4.medical_clinic.model.Facility;
import com.kanapa4.medical_clinic.model.FacilityDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    @Mapping(target = "doctorIds", source = "doctors")
    Facility toPojo(FacilityEntity entity);

    @Mapping(target = "doctors", ignore = true)
    FacilityEntity toEntity(Facility facility);

    @Mapping(target = "doctorsId", source = "doctorIds")
    FacilityDto toDto(Facility facility);

    default Long doctorToId(DoctorEntity doctorEntity) {
        return doctorEntity.getId();
    }
}
