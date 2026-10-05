package com.kanapa4.medical_clinic.adapter.user;

import com.kanapa4.medical_clinic.domain.port.UserMapperPort;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.User;
import com.kanapa4.medical_clinic.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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
        if (userPage == null) {
            return null;
        }
        List<UserDto> dtos = (userPage.getContent() == null)
                ? List.of()
                : userPage.getContent().stream()
                  .map(this::toDto)
                  .toList();
        return Page.<UserDto>builder()
                .content(dtos)
                .page(userPage.getPage())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .build();
    }
}
