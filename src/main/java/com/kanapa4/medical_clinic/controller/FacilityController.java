package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.model.dto.FacilityCreateCommand;
import com.kanapa4.medical_clinic.model.dto.FacilityDto;
import com.kanapa4.medical_clinic.service.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Facility Controller", description = "Operations related to managing medical facilities")
@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @Operation(summary = "Get paginated list of facilities", description = "Retrieves facilities with pagination and optional sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of facilities")
    })
    @GetMapping
    public Page<FacilityDto> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return facilityService.getPaginatedFacilities(page, size, sortBy);
    }

    @Operation(summary = "Get facility by ID", description = "Retrieves a single facility by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility found and returned"),
            @ApiResponse(responseCode = "404", description = "Facility not found")
    })
    @GetMapping("/{id}")
    public FacilityDto findById(@PathVariable Long id) {
        return facilityService.findById(id);
    }

    @Operation(summary = "Create a new facility", description = "Creates a new medical facility record in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Facility successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacilityDto create(@RequestBody FacilityCreateCommand dto) {
        return facilityService.create(dto);
    }

    @Operation(summary = "Update an existing facility", description = "Updates facility information by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Facility not found")
    })
    @PutMapping("/{id}")
    public FacilityDto update(@PathVariable Long id, @RequestBody FacilityDto dto) {
        return facilityService.update(id, dto);
    }

    @Operation(summary = "Delete facility by ID", description = "Deletes a facility from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Facility successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Facility not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        facilityService.delete(id);
    }
}
