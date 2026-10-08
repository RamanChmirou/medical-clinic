package com.kanapa4.medical_clinic.adapter.doctor;

import com.kanapa4.medical_clinic.adapter.DoctorEntity;
import com.kanapa4.medical_clinic.adapter.FacilityEntity;
import com.kanapa4.medical_clinic.model.Doctor;
import com.kanapa4.medical_clinic.model.DoctorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "facilityIds", source = "facilities")
    Doctor toPojo(DoctorEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "facilities", ignore = true)
    DoctorEntity toEntity(Doctor domain);

    DoctorDto toDto(Doctor doctor);

    default Long facilityToId(FacilityEntity facilityEntity) {
        return facilityEntity != null ? facilityEntity.getId() : null;
    }
}