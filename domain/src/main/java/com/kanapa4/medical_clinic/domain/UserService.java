package com.kanapa4.medical_clinic.domain;

import com.kanapa4.medical_clinic.domain.port.UserMapperPort;
import com.kanapa4.medical_clinic.domain.port.UserRepositoryPort;
import com.kanapa4.medical_clinic.model.*;
import com.kanapa4.medical_clinic.model.exception.UserAlreadyExistsException;
import com.kanapa4.medical_clinic.model.exception.UserDoesNotExistsException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService {
    private final UserRepositoryPort userRepositoryPort;
    private final UserMapperPort userMapperPort;

    public Page<UserDto> getPaginatedUsers(Pageable pageable) {
        return userMapperPort.toPageDto(userRepositoryPort.findAll(pageable));
    }

    public UserDto create(UserCreateCommand command) {
        if (userRepositoryPort.findByEmail(command.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = User.builder()
                .email(command.getEmail())
                .password(command.getPassword())
                .role(command.getRole())
                .build();

        User savedUser = userRepositoryPort.save(user);
        return userMapperPort.toDto(savedUser);
    }

    public UserDto update(String email, UserDto dto) {
        User existing = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserDoesNotExistsException("User does not exist"));

        if (!existing.getEmail().equals(dto.getEmail())
                && userRepositoryPort.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("New email is already in use");
        }

        existing.setEmail(dto.getEmail());
        existing.setRole(dto.getRole());
        User updatedUser = userRepositoryPort.save(existing);
        return userMapperPort.toDto(updatedUser);
    }

    public void delete(String email) {
        if (userRepositoryPort.findByEmail(email).isEmpty()) {
            throw new UserDoesNotExistsException("User does not exist");
        }
        userRepositoryPort.deleteByEmail(email);
    }

    public void editPassword(String email, String password) {
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserDoesNotExistsException("User does not exist"));
        user.setPassword(password);
        userRepositoryPort.save(user);
    }
}