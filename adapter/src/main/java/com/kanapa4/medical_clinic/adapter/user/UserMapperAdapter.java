package com.kanapa4.medical_clinic.adapter.user;

import com.kanapa4.medical_clinic.domain.port.UserMapperPort;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.User;
import com.kanapa4.medical_clinic.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperAdapter implements UserMapperPort {

    private final UserMapper userMapper;

    @Override
    public UserDto toDto(User user) {
        return userMapper.toDto(user);
    }

    @Override
    public Page<UserDto> toPageDto(Page<User> userPage) {
        return userPage == null ? null : userPage.map(this::toDto);
    }
}
