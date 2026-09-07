package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.model.dto.PatientCreateCommand;
import com.kanapa4.medical_clinic.model.dto.PatientDto;
import com.kanapa4.medical_clinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Patient Controller", description = "Operations related to managing patients")
@RequiredArgsConstructor
@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;

    @Operation(summary = "Get paginated list of patients", description = "Retrieves patients with pagination and optional sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of patients")
    })
    @GetMapping
    public Page<PatientDto> getPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return patientService.getPaginatedPatients(page, size, sortBy);
    }

    @Operation(summary = "Get patient by ID", description = "Retrieves a single patient by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found and returned"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping("/{id}")
    public PatientDto findByIdCardNo(@PathVariable Long id) {
        return patientService.findById(id);
    }

    @Operation(summary = "Create a new patient", description = "Creates a new patient record in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "409", description = "Patient already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto add(@RequestBody PatientCreateCommand patient) {
        return patientService.create(patient);
    }

    @Operation(summary = "Add patient to user account", description = "Creates a new patient and links it to the email provided in the URL path.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient successfully created and linked"),
            @ApiResponse(responseCode = "404", description = "User account not found")
    })
    @PostMapping("/user/{email}")
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto addToUserAccount(@PathVariable String email, @RequestBody PatientCreateCommand patient) { return patientService.createPatientForUser(email, patient); }

    @Operation(summary = "Update an existing patient", description = "Updates patient information by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PutMapping("/{id}")
    public PatientDto update(@PathVariable Long id, @RequestBody PatientDto patient) {
        return patientService.update(id, patient);
    }

    @Operation(summary = "Delete patient by ID", description = "Deletes a patient from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        patientService.delete(id);
    }
}
