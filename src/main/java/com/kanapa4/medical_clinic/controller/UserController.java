package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.model.EditPasswordCommand;
import com.kanapa4.medical_clinic.model.dto.UserCreateCommand;
import com.kanapa4.medical_clinic.model.dto.UserDto;
import com.kanapa4.medical_clinic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Page<UserDto> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return userService.getPaginatedUsers(page, size, sortBy);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@RequestBody UserCreateCommand patient) {
        return userService.create(patient);
    }

    @PutMapping("/{email}")
    public UserDto update(@PathVariable String email, @RequestBody UserDto patient) {
        return userService.update(email, patient);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String email) {
        userService.delete(email);
    }

    @PatchMapping("/{email}")
    public void editPassword(@PathVariable String email, @RequestBody EditPasswordCommand password) {
        userService.editPassword(email, password.getPassword());
    }
}
