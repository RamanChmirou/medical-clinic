package com.kanapa4.medical_clinic;

import com.kanapa4.medical_clinic.domain.UserService;
import com.kanapa4.medical_clinic.domain.port.UserMapperPort;
import com.kanapa4.medical_clinic.domain.port.UserRepositoryPort;
import com.kanapa4.medical_clinic.model.*;
import com.kanapa4.medical_clinic.model.exception.UserAlreadyExistsException;
import com.kanapa4.medical_clinic.model.exception.UserDoesNotExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepositoryPort userRepositoryPort;
    private UserMapperPort userMapperPort;
    private UserService userService;

    @BeforeEach
    void setup() {
        this.userRepositoryPort = mock(UserRepositoryPort.class);
        this.userMapperPort = mock(UserMapperPort.class);
        this.userService = new UserService(userRepositoryPort, userMapperPort);
    }

    @Test
    void getPaginatedUsers_DataCorrect_ReturnPaginatedUserDtos() {
        // given
        Pageable pageable = new Pageable(0, 15);
        User user = User.builder()
                .email("email@com")
                .role(Role.DOCTOR)
                .build();
        UserDto userDto = UserDto.builder()
                .email("email@com")
                .role(Role.DOCTOR)
                .build();

        Page<User> domainPage = Page.<User>builder()
                .content(List.of(user))
                .page(0)
                .size(15)
                .totalElements(1L)
                .totalPages(1)
                .build();

        Page<UserDto> dtoPage = Page.<UserDto>builder()
                .content(List.of(userDto))
                .page(0)
                .size(15)
                .totalElements(1L)
                .totalPages(1)
                .build();

        when(userRepositoryPort.findAll(pageable)).thenReturn(domainPage);
        when(userMapperPort.toPageDto(domainPage)).thenReturn(dtoPage);

        // when
        Page<UserDto> result = userService.getPaginatedUsers(pageable);

        // then
        assertAll(
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertEquals("email@com", result.getContent().getFirst().getEmail())
        );
        verify(userRepositoryPort, times(1)).findAll(pageable);
        verify(userMapperPort, times(1)).toPageDto(domainPage);
        verifyNoMoreInteractions(userRepositoryPort);
    }

    @Test
    void create_DataCorrect_ReturnCreatedUserDto() {
        // given
        UserCreateCommand userCreateCommand = UserCreateCommand.builder()
                .email("email")
                .password("password")
                .role(Role.DOCTOR)
                .build();
        when(userRepositoryPort.findByEmail(userCreateCommand.getEmail())).thenReturn(Optional.empty());

        User savedUser = User.builder()
                .email("email")
                .password("password")
                .role(Role.DOCTOR)
                .build();
        UserDto expectedDto = UserDto.builder()
                .email("email")
                .role(Role.DOCTOR)
                .build();

        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);
        when(userMapperPort.toDto(savedUser)).thenReturn(expectedDto);

        // when
        UserDto result = userService.create(userCreateCommand);

        // then
        assertAll(
                () -> assertEquals("email", result.getEmail()),
                () -> assertEquals(Role.DOCTOR, result.getRole())
        );
        verify(userRepositoryPort, times(1)).findByEmail(userCreateCommand.getEmail());
        verify(userRepositoryPort, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepositoryPort);
    }

    @Test
    void update_DataCorrect_ReturnUpdatedUserDto() {
        // given
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

        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(userForUpdate));
        when(userRepositoryPort.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(userRepositoryPort.save(any(User.class))).thenReturn(updatedUser);
        when(userMapperPort.toDto(updatedUser)).thenReturn(userDto);

        // when
        UserDto result = userService.update(email, userDto);

        // then
        assertAll(
                () -> assertEquals(userDto.getEmail(), result.getEmail()),
                () -> assertEquals(userDto.getRole(), result.getRole())
        );
        verify(userRepositoryPort, times(1)).findByEmail(email);
        verify(userRepositoryPort, times(1)).save(any(User.class));
    }

    @Test
    void create_UserAlreadyExists_ThrowUserAlreadyExistsException() {
        // given
        UserCreateCommand command = UserCreateCommand.builder()
                .email("test@email.com")
                .build();
        User existingUser = User.builder().build();
        when(userRepositoryPort.findByEmail(command.getEmail())).thenReturn(Optional.of(existingUser));

        // when
        UserAlreadyExistsException result = assertThrows(UserAlreadyExistsException.class,
                () -> userService.create(command));

        // then
        assertEquals("User already exists", result.getMessage());
    }

    @Test
    void update_UserDoesNotExist_ThrowUserDoesNotExistsException() {
        // given
        UserDto dto = UserDto.builder().build();
        when(userRepositoryPort.findByEmail("old@email.com")).thenReturn(Optional.empty());

        // when
        UserDoesNotExistsException result = assertThrows(UserDoesNotExistsException.class,
                () -> userService.update("old@email.com", dto));

        // then
        assertEquals("User does not exist", result.getMessage());
    }

    @Test
    void update_NewEmailAlreadyInUse_ThrowUserAlreadyExistsException() {
        // given
        UserDto dto = UserDto.builder()
                .email("occupied@email.com")
                .build();
        User existingUser = User.builder()
                .email("old@email.com")
                .build();
        User occupant = User.builder().build();
        when(userRepositoryPort.findByEmail("old@email.com")).thenReturn(Optional.of(existingUser));
        when(userRepositoryPort.findByEmail(dto.getEmail())).thenReturn(Optional.of(occupant));

        // when
        UserAlreadyExistsException result = assertThrows(UserAlreadyExistsException.class,
                () -> userService.update("old@email.com", dto));

        // then
        assertEquals("New email is already in use", result.getMessage());
    }

    @Test
    void editPassword_UserDoesNotExist_ThrowUserDoesNotExistsException() {
        // given
        when(userRepositoryPort.findByEmail("test@email.com")).thenReturn(Optional.empty());

        // when
        UserDoesNotExistsException result = assertThrows(UserDoesNotExistsException.class,
                () -> userService.editPassword("test@email.com", "newPassword"));

        // then
        assertEquals("User does not exist", result.getMessage());
    }

    @Test
    void delete_DataCorrect_DeleteUser() {
        // given
        User user = User.builder()
                .email("email")
                .build();
        String email = user.getEmail();
        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        userService.delete(email);

        // then
        verify(userRepositoryPort, times(1)).findByEmail(email);
        verify(userRepositoryPort, times(1)).deleteByEmail(email);
        verifyNoMoreInteractions(userRepositoryPort);
    }

    @Test
    void editPassword_DataCorrect_EditPassword() {
        // given
        String email = "email";
        String newPassword = "6666";
        User user = User.builder()
                .email(email)
                .password("1111")
                .build();
        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepositoryPort.save(user)).thenReturn(user);

        // when
        userService.editPassword(email, newPassword);

        // then
        assertEquals(newPassword, user.getPassword());
        verify(userRepositoryPort, times(1)).findByEmail(email);
        verify(userRepositoryPort, times(1)).save(user);
        verifyNoMoreInteractions(userRepositoryPort);
    }

    @Test
    void delete_UserDoesNotExists_ThrowUserDoesNotExistsException() {
        // given
        when(userRepositoryPort.findByEmail("email")).thenReturn(Optional.empty());

        // when
        UserDoesNotExistsException result = assertThrows(UserDoesNotExistsException.class,
                () -> userService.delete("email"));

        // then
        assertEquals("User does not exist", result.getMessage());
    }
}