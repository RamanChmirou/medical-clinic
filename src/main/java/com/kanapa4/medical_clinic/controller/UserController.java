package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.model.EditPasswordCommand;
import com.kanapa4.medical_clinic.model.dto.UserCreateCommand;
import com.kanapa4.medical_clinic.model.dto.UserDto;
import com.kanapa4.medical_clinic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@RequestBody UserCreateCommand patient) {
        return userService.create(patient);
    }

    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@PathVariable String email, @RequestBody UserDto patient) {
        return userService.update(email, patient);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String email) {
        userService.delete(email);
    }

    @PatchMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public void editPassword(@PathVariable String email, @RequestBody EditPasswordCommand password) {
        userService.editPassword(email, password.getPassword());
    }
}
