package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.exception.UserAlreadyExistsException;
import com.kanapa4.medical_clinic.exception.UserDoesNotExistsException;
import com.kanapa4.medical_clinic.mapper.UserMapper;
import com.kanapa4.medical_clinic.model.Role;
import com.kanapa4.medical_clinic.model.dto.UserCreateCommand;
import com.kanapa4.medical_clinic.model.dto.UserDto;
import com.kanapa4.medical_clinic.model.entity.User;
import com.kanapa4.medical_clinic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserService userService;

    @BeforeEach
    void setup() {
        this.userRepository = mock(UserRepository.class);
        this.userMapper = getMapper(UserMapper.class);
        this.userService = new UserService(userRepository, userMapper);
    }

    @Test
    void getPaginatedUsers_DataCorrect_ReturnPaginatedUserDtos() {
        //given
        Pageable pageable = PageRequest.of(0, 15, Sort.by("email").ascending());
        User user = User.builder()
                .email("email@com")
                .role(Role.DOCTOR)
                .build();
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        //when
        Page<UserDto> result = userService.getPaginatedUsers(0, 15, "email");
        //then
        assertAll(
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertEquals("email@com", result.getContent().getFirst().getEmail())
        );
        verify(userRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void create_DataCorrect_ReturnCreatedUserDto() {
        //given
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
        //when
        UserDto result = userService.create(userCreateCommand);
        //then
        assertAll(
                () -> assertEquals("email", result.getEmail()),
                () -> assertEquals(Role.DOCTOR, result.getRole())
        );
        verify(userRepository, times(1)).findByEmail(userCreateCommand.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void update_DataCorrect_ReturnUpdatedUserDto() {
        //given
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
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        //when
        UserDto result = userService.update(email, userDto);
        //then
        assertAll(
                () -> assertEquals(userDto.getEmail(), result.getEmail()),
                () -> assertEquals(userDto.getRole(), result.getRole())
        );
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void create_UserAlreadyExists_ThrowUserAlreadyExistsException() {
        //given
        UserCreateCommand command = UserCreateCommand.builder()
                .email("test@email.com")
                .build();
        User existingUser = User.builder().build();
        when(userRepository.findByEmail(command.getEmail())).thenReturn(Optional.of(existingUser));
        //when
        UserAlreadyExistsException result = assertThrows(UserAlreadyExistsException.class,
                () -> userService.create(command));
        //then
        assertAll(
                () -> assertEquals("User already exists", result.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, result.getHttpStatus())
        );
    }

    @Test
    void update_UserDoesNotExist_ThrowUserDoesNotExistsException() {
        //given
        UserDto dto = UserDto.builder().build();
        when(userRepository.findByEmail("old@email.com")).thenReturn(Optional.empty());
        //when
        UserDoesNotExistsException result = assertThrows(UserDoesNotExistsException.class,
                () -> userService.update("old@email.com", dto));
        //then
        assertAll(
                () -> assertEquals("User does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void update_NewEmailAlreadyInUse_ThrowUserAlreadyExistsException() {
        //given
        UserDto dto = UserDto.builder()
                .email("occupied@email.com")
                .build();
        User existingUser = User.builder()
                .email("old@email.com")
                .build();
        User occupant = User.builder().build();
        when(userRepository.findByEmail("old@email.com")).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(occupant));
        //when
        UserAlreadyExistsException result = assertThrows(UserAlreadyExistsException.class,
                () -> userService.update("old@email.com", dto));
        //then
        assertAll(
                () -> assertEquals("New email is already in use", result.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, result.getHttpStatus())
        );
    }

    @Test
    void editPassword_UserDoesNotExist_ThrowUserDoesNotExistsException() {
        //given
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.empty());
        //when
        UserDoesNotExistsException result = assertThrows(UserDoesNotExistsException.class,
                () -> userService.editPassword("test@email.com", "newPassword"));
        //then
        assertAll(
                () -> assertEquals("User does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void delete_DataCorrect_DeleteUser() {
        //given
        User user = User.builder()
                .email("email")
                .build();
        String email = user.getEmail();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        //when
        userService.delete(email);
        //then
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).deleteByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void editPassword_DataCorrect_EditPassword() {
        //given
        String email = "email";
        String newPassword = "6666";
        User user = User.builder()
                .email(email)
                .password("1111")
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        //when
        userService.editPassword(email, newPassword);
        //then
        assertEquals(newPassword, user.getPassword());
        verify(userRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void delete_UserDoesNotExists_ThrowUserDoesNotExistsException() {
        //given
        when(userRepository.findByEmail("email")).thenReturn(Optional.empty());
        //when
        UserDoesNotExistsException result = assertThrows(UserDoesNotExistsException.class,
                () -> userService.delete("email"));
        //then
        assertAll(
                () -> assertEquals("User does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }
}
