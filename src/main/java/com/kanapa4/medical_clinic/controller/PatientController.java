package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.model.EditPasswordCommand;
import com.kanapa4.medical_clinic.model.Patient;
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
    public List<Patient> findAll() {
        return patientService.findAll();
    }

    @GetMapping("/{email}")
    public Patient findByEmail(@PathVariable String email) {
        return patientService.findByEmail(email);
    }

    @PostMapping("/create")
    public Patient add(@RequestBody Patient patient) {
        return patientService.create(patient);
    }

    @PutMapping("/{email}")
    public Patient update(@PathVariable String email, @RequestBody Patient patient) {
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
