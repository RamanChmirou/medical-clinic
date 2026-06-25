package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.exception.UserAlreadyExistsException;
import com.kanapa4.medical_clinic.exception.UserDoesNotExistsException;
import com.kanapa4.medical_clinic.mapper.UserMapper;
import com.kanapa4.medical_clinic.model.Role;
import com.kanapa4.medical_clinic.model.dto.UserCreateCommand;
import com.kanapa4.medical_clinic.model.dto.UserDto;
import com.kanapa4.medical_clinic.model.entity.User;
import com.kanapa4.medical_clinic.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Page<UserDto> getPaginatedUsers(int page, int size, String sortBy) {
        if (size > 30) {
            size = 30;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());

        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    public UserDto create(UserCreateCommand dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(dto.getRole())
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public UserDto update(String email, UserDto dto) {
        User existing = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserDoesNotExistsException("User does not exist"));

        existing.setEmail(dto.getEmail());
        existing.setRole(dto.getRole());
        User updatedUser = userRepository.save(existing);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void delete(String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new UserDoesNotExistsException("User does not exist");
        }
        userRepository.deleteByEmail(email);
    }

    @Transactional
    public void editPassword(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserDoesNotExistsException("User does not exist"));
        user.setPassword(password);
    }
}
