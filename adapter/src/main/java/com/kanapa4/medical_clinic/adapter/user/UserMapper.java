package com.kanapa4.medical_clinic.adapter.user;

import com.kanapa4.medical_clinic.adapter.UserEntity;
import com.kanapa4.medical_clinic.model.User;
import com.kanapa4.medical_clinic.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toPojo(UserEntity entity);

    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patients", ignore = true)
    UserEntity toEntity(User user);
}