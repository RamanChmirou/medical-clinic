package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.model.dto.PatientCreateCommand;
import com.kanapa4.medical_clinic.model.dto.PatientDto;
import com.kanapa4.medical_clinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public Page<PatientDto> getPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return patientService.getPaginatedPatients(page, size, sortBy);
    }

    @GetMapping("/{id}")
    public PatientDto findByIdCardNo(@PathVariable Long id) {
        return patientService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto add(@RequestBody PatientCreateCommand patient) {
        return patientService.create(patient);
    }

    @PutMapping("/{id}")
    public PatientDto update(@PathVariable Long id, @RequestBody PatientDto patient) {
        return patientService.update(id, patient);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        patientService.delete(id);
    }
}
