package com.kanapa4.medical_clinic.model.dto;

import com.kanapa4.medical_clinic.model.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCreateCommand {
    private String email;
    private String password;
    private Role role;
}
