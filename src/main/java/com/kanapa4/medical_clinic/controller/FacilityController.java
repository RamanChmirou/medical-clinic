package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.model.dto.FacilityCreateCommand;
import com.kanapa4.medical_clinic.model.dto.FacilityDto;
import com.kanapa4.medical_clinic.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @GetMapping
    public Page<FacilityDto> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return facilityService.getPaginatedFacilities(page, size, sortBy);
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
