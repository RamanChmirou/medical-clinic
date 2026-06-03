package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.model.dto.DoctorCreateCommand;
import com.kanapa4.medical_clinic.model.dto.DoctorDto;
import com.kanapa4.medical_clinic.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping
    public List<DoctorDto> findAll() {
        return doctorService.findAll();
    }

    @GetMapping("/{id}")
    public DoctorDto findById(@PathVariable Long id) {
        return doctorService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto add(@RequestBody DoctorCreateCommand doctor) {
        return doctorService.create(doctor);
    }

    @PutMapping("/{id}")
    public DoctorDto update(@PathVariable Long id, @RequestBody DoctorDto doctor) {
        return doctorService.update(id, doctor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        doctorService.delete(id);
    }

    @PostMapping("/{doctorId}/facilities/{facilityId}")
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto addFacilityToDoctor(@PathVariable Long doctorId, @PathVariable Long facilityId) {
        return doctorService.addFacilityToDoctor(doctorId, facilityId);
    }

    @DeleteMapping("/{doctorId}/facilities/{facilityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public DoctorDto removeFacilityFromDoctor(@PathVariable Long doctorId, @PathVariable Long facilityId) {
        return doctorService.removeFacilityFromDoctor(doctorId, facilityId);
    }
}
