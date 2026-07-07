package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.mapper.UserMapper;
import com.kanapa4.medical_clinic.model.Role;
import com.kanapa4.medical_clinic.model.dto.UserCreateCommand;
import com.kanapa4.medical_clinic.model.dto.UserDto;
import com.kanapa4.medical_clinic.model.entity.User;
import com.kanapa4.medical_clinic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserService userService;

    @BeforeEach
    void setup() {
        this.userRepository = Mockito.mock(UserRepository.class);
        this.userMapper = Mappers.getMapper(UserMapper.class);
        this.userService = new UserService(userRepository, userMapper);
    }

    @Test
    void getPaginatedUsers_DataCorrect_ReturnPaginatedUserDtos() {
        //given - w tej sekcji przygotowujem dane do testu
        Pageable pageable = PageRequest.of(0, 15, Sort.by("email").ascending());
        User user = User.builder()
                .email("email@com")
                .role(Role.DOCTOR)
                .build();
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        //when - w tej sekcji przeprowadzam sam test
        Page<UserDto> result = userService.getPaginatedUsers(0,15, "email");
        //then - tutaj sprawdzam wyniki testu
        assertAll(
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertEquals("email@com", result.getContent().getFirst().getEmail())
        );
    }

    @Test
    void create_DataCorrect_ReturnCreatedUserDto() {
        //given - w tej sekcji przygotowujem dane do testu
        UserCreateCommand userCreateCommand = UserCreateCommand.builder()
                .email("email")
                .password("password")
                .role(Role.DOCTOR)
                .build();
        when(userRepository.findByEmail(userCreateCommand.getEmail())).thenReturn(Optional.empty());
        User savedUser = User.builder()
                .email("email")
                .password("password")
                .role(Role.DOCTOR)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        //when - w tej sekcji przeprowadzam sam test
        UserDto result = userService.create(userCreateCommand);
        //then - tutaj sprawdzam wyniki testu
        assertAll(
                () -> assertEquals("email", result.getEmail()),
                () -> assertEquals(Role.DOCTOR, result.getRole())
        );
    }

    @Test
    void update_DataCorrect_ReturnUpdatedUserDto() {
        //given - przygotowanie danych
        String email = "email";
        User userForUpdate = User.builder()
                .email("email")
                .role(Role.PATIENT)
                .build();
        UserDto userDto = UserDto.builder()
                .email("newEmail")
                .role(Role.DOCTOR)
                .build();
        User updatedUser = User.builder()
                .email("newEmail")
                .role(Role.DOCTOR)
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userForUpdate));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        //when - w tej sekcji przeprowadzam test
        UserDto result = userService.update(email, userDto);
        //then - sprawdzam wyniki testu
        assertAll(
                () -> assertEquals(userDto.getEmail(), result.getEmail()),
                () -> assertEquals(userDto.getRole(), result.getRole())
        );
    }
}
