package com.kanapa4.medical_clinic.domain.port;

import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.User;
import com.kanapa4.medical_clinic.model.UserDto;


public interface UserMapperPort {
    UserDto toDto(User user);

    Page<UserDto> toPageDto(Page<User> userPage);
}
