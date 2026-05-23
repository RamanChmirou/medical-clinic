package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.model.EditPasswordCommand;
import com.kanapa4.medical_clinic.model.dto.PatientCreatedDto;
import com.kanapa4.medical_clinic.model.dto.PatientDto;
import com.kanapa4.medical_clinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;

    @GetMapping("/all")
    public List<PatientDto> findAll() {
        return patientService.findAll();
    }

    @GetMapping("/{email}")
    public PatientDto findByEmail(@PathVariable String email) {
        return patientService.findByEmail(email);
    }

    @PostMapping("/create")
    public PatientDto add(@RequestBody PatientCreatedDto patient) {
        return patientService.create(patient);
    }

    @PutMapping("/{email}")
    public PatientDto update(@PathVariable String email, @RequestBody PatientDto patient) {
        return patientService.update(email, patient);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String email) {
        patientService.delete(email);
    }

    @PatchMapping("/{email}")
    public void editPassword(@PathVariable String email, @RequestBody EditPasswordCommand password) {
        patientService.editPassword(email, password.getPassword());
    }
}
