package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.model.EditPasswordCommand;
import com.kanapa4.medical_clinic.model.dto.UserCreateCommand;
import com.kanapa4.medical_clinic.model.dto.UserDto;
import com.kanapa4.medical_clinic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Controller", description = "Operations related to managing users")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get paginated list of users", description = "Retrieves users with pagination and optional sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    })
    @GetMapping
    public Page<UserDto> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return userService.getPaginatedUsers(page, size, sortBy);
    }

    @Operation(summary = "Create a new user", description = "Creates a new user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "409", description = "User with this email already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@RequestBody UserCreateCommand patient) {
        return userService.create(patient);
    }

    @Operation(summary = "Update an existing user", description = "Updates user information by email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{email}")
    public UserDto update(@PathVariable String email, @RequestBody UserDto patient) {
        return userService.update(email, patient);
    }

    @Operation(summary = "Delete user by email", description = "Deletes a user from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String email) {
        userService.delete(email);
    }

    @Operation(summary = "Change user password", description = "Changes the password for the specified user email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{email}")
    public void editPassword(@PathVariable String email, @RequestBody EditPasswordCommand password) {
        userService.editPassword(email, password.getPassword());
    }
}
