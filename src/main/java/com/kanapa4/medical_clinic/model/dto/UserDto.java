package com.kanapa4.medical_clinic.model.dto;

import com.kanapa4.medical_clinic.model.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
    private Long id;
    private String email;
    private Role role;
}
