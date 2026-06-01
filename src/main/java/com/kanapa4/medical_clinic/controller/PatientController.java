package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.model.dto.PatientCreateCommand;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PatientDto> findAll() {
        return patientService.findAll();
    }

    @GetMapping("/user/{email}")
    @ResponseStatus(HttpStatus.OK)
    public List<PatientDto> findAllByUserEmail(@PathVariable String email) {
        return patientService.findAllByUserEmail(email);
    }

    @GetMapping("/{idCardNo}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto findByIdCardNo(@PathVariable String idCardNo) {
        return patientService.findByIdCardNo(idCardNo);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto add(@RequestBody PatientCreateCommand patient) {
        return patientService.create(patient);
    }

    @PutMapping("/{idCardNo}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto update(@PathVariable String idCardNo, @RequestBody PatientDto patient) {
        return patientService.update(idCardNo, patient);
    }

    @DeleteMapping("/{idCardNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String idCardNo) {
        patientService.delete(idCardNo);
    }
}
