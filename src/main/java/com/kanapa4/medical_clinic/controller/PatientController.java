package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.entity.Patient;
import com.kanapa4.medical_clinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PatientController {
    private final PatientService patientService;

    @GetMapping("/patients")
    public List<Patient> findAll (){
        return patientService.findAll();
    }
}
