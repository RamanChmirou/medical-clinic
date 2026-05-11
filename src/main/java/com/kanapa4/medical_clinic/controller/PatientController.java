package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.entity.Patient;
import com.kanapa4.medical_clinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PatientController {
    private final PatientService patientService;

    @GetMapping("/patients")
    public List<Patient> findAll (){
        return patientService.findAll();
    }

    @GetMapping("/patient/{email}")
    public Patient findByEmail (@PathVariable("email") String email) {
        return patientService.findByEmail(email);
    }

    @PostMapping
    public Patient add(@RequestBody Patient patient) {
        return patientService.create(patient);
    }

    @PutMapping("/{email}")
    public Patient update(@PathVariable String email, @RequestBody Patient patient) {
        return patientService.update(email, patient);
    }

    @DeleteMapping("/{email}")
    public void delete(@PathVariable String email) {
        patientService.delete(email);
    }
}
