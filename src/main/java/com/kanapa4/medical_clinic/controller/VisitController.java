package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.model.dto.VisitCreateCommand;
import com.kanapa4.medical_clinic.model.dto.VisitDto;
import com.kanapa4.medical_clinic.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Visit Controller", description = "Operations related to managing medical visits and appointments")
@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;

    @Operation(summary = "Get paginated list of visits", description = "Retrieves visits with pagination and optional sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of visits")
    })
    @GetMapping
    public Page<VisitDto> getVisits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return visitService.getPaginatedVisits(page, size, sortBy);
    }

    @Operation(summary = "Create a new visit slot", description = "Creates a new visit slot/appointment slot in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Visit slot successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or visit details validation failed"),
            @ApiResponse(responseCode = "409", description = "Visit slot already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitDto createVisitSlot(@RequestBody VisitCreateCommand command) {
        return visitService.createVisitSlot(command);
    }

    @Operation(summary = "Update visit slot details", description = "Updates the details of a visit slot by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visit slot successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Visit slot not found")
    })
    @PutMapping("/{id}")
    public VisitDto update(@PathVariable Long id, @RequestBody VisitCreateCommand visit) {
        return visitService.updateVisit(id, visit);
    }

    @Operation(summary = "Delete visit slot by ID", description = "Deletes a visit slot from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visit slot successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Visit slot not found")
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        visitService.delete(id);
    }

    @Operation(summary = "Book a visit slot for a patient", description = "Assigns/books an available visit slot to a patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visit successfully booked"),
            @ApiResponse(responseCode = "400", description = "Visit already booked or unavailable"),
            @ApiResponse(responseCode = "404", description = "Visit slot or patient not found")
    })
    @PatchMapping("/{visitId}/book/{patientId}")
    public VisitDto bookVisit(@PathVariable Long visitId, @PathVariable Long patientId) {
        return visitService.bookVisit(visitId, patientId);
    }

    @Operation(summary = "Get list of visits for a patient", description = "Retrieves a list of all visits booked for a specific patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved patient's visits"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping("/patient/{patientId}")
    public List<VisitDto> getPatientVisits(@PathVariable Long patientId) {
        return visitService.getPatientVisits(patientId);
    }
}