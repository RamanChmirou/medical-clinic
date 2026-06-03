package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.model.dto.FacilityCreateCommand;
import com.kanapa4.medical_clinic.model.dto.FacilityDto;
import com.kanapa4.medical_clinic.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @GetMapping
    public List<FacilityDto> findAll() {
        return facilityService.findAll();
    }

    @GetMapping("/{id}")
    public FacilityDto findById(@PathVariable Long id) {
        return facilityService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacilityDto create(@RequestBody FacilityCreateCommand dto) {
        return facilityService.create(dto);
    }

    @PutMapping("/{id}")
    public FacilityDto update(@PathVariable Long id, @RequestBody FacilityDto dto) {
        return facilityService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        facilityService.delete(id);
    }
}
