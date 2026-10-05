package com.kanapa4.medical_clinic.adapter.visit;

import com.kanapa4.medical_clinic.domain.VisitService;
import com.kanapa4.medical_clinic.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Visit Controller", description = "Operations related to managing medical visits and appointments")
@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @Operation(summary = "Get paginated list of visits", description = "Retrieves visits with pagination and optional filtering.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of visits")
    })
    @GetMapping
    public Page<VisitDto> getVisits(
            @ModelAttribute VisitFilter filter,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = new Pageable(page, size);
        return visitService.searchVisits(filter, pageable);
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
    public VisitDto update(
            @PathVariable(name = "id") Long id,
            @RequestBody VisitCreateCommand visit
    ) {
        return visitService.updateVisit(id, visit);
    }

    @Operation(summary = "Delete visit slot by ID", description = "Deletes a visit slot from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Visit slot successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Visit slot not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(name = "id") Long id) {
        visitService.delete(id);
    }

    @Operation(summary = "Book a visit slot for a patient", description = "Assigns/books an available visit slot to a patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visit successfully booked"),
            @ApiResponse(responseCode = "400", description = "Visit already booked or unavailable"),
            @ApiResponse(responseCode = "404", description = "Visit slot or patient not found")
    })
    @PatchMapping("/{visitId}/book/{patientId}")
    public VisitDto bookVisit(
            @PathVariable(name = "visitId") Long visitId,
            @PathVariable(name = "patientId") Long patientId
    ) {
        return visitService.bookVisit(visitId, patientId);
    }

    @Operation(summary = "Cancel a visit", description = "Cancels a booked visit, making the slot available again.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visit successfully cancelled"),
            @ApiResponse(responseCode = "400", description = "Visit cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Visit not found")
    })
    @PatchMapping("/{visitId}/cancel")
    public VisitDto cancelVisit(@PathVariable(name = "visitId") Long visitId) {
        return visitService.cancelVisit(visitId);
    }
}