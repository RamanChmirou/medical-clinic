package com.kanapa4.medical_clinic.adapter.doctor;

import com.kanapa4.medical_clinic.domain.DoctorService;
import com.kanapa4.medical_clinic.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Doctor Controller", description = "Operations related to managing doctors")
@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @Operation(summary = "Get paginated list of doctors", description = "Retrieves doctors with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of doctors")
    })
    @GetMapping
    public Page<DoctorDto> getDoctors(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "specialization", required = false) Specialization specialization
    ) {
        Pageable pageable = new Pageable(page, Math.min(size, 30));
        return doctorService.getPaginatedDoctors(pageable, specialization);
    }

    @Operation(summary = "Get doctor by ID", description = "Retrieves a single doctor by their unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found and returned"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping("/{id}")
    public DoctorDto findById(@PathVariable(name = "id") Long id) {
        return doctorService.findById(id);
    }

    @Operation(summary = "Create a new doctor", description = "Creates a new doctor record in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or data validation failed"),
            @ApiResponse(responseCode = "409", description = "Doctor with this information already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto add(@RequestBody DoctorCreateCommand doctor) {
        return doctorService.create(doctor);
    }

    @Operation(summary = "Update an existing doctor", description = "Updates the doctor information by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @PutMapping("/{id}")
    public DoctorDto update(
            @PathVariable(name = "id") Long id,
            @RequestBody DoctorDto doctor
    ) {
        return doctorService.update(id, doctor);
    }

    @Operation(summary = "Delete doctor by ID", description = "Deletes a doctor from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Doctor successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(name = "id") Long id) {
        doctorService.delete(id);
    }

    @Operation(summary = "Assign facility to doctor", description = "Associates a facility with a doctor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Facility successfully assigned to doctor"),
            @ApiResponse(responseCode = "404", description = "Doctor or Facility not found")
    })
    @PostMapping("/{doctorId}/facilities/{facilityId}")
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto addFacilityToDoctor(
            @PathVariable(name = "doctorId") Long doctorId,
            @PathVariable(name = "facilityId") Long facilityId
    ) {
        return doctorService.addFacilityToDoctor(doctorId, facilityId);
    }

    @Operation(summary = "Remove facility assignment from doctor", description = "Removes the association between a facility and a doctor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility assignment successfully removed"),
            @ApiResponse(responseCode = "404", description = "Doctor or Facility not found")
    })
    @DeleteMapping("/{doctorId}/facilities/{facilityId}")
    public DoctorDto removeFacilityFromDoctor(
            @PathVariable(name = "doctorId") Long doctorId,
            @PathVariable(name = "facilityId") Long facilityId
    ) {
        return doctorService.removeFacilityFromDoctor(doctorId, facilityId);
    }
}