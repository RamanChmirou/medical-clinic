package com.kanapa4.medical_clinic.controller;

import com.kanapa4.medical_clinic.model.dto.VisitCreateCommand;
import com.kanapa4.medical_clinic.model.dto.VisitDto;
import com.kanapa4.medical_clinic.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitDto createVisitSlot(@RequestBody VisitCreateCommand command) {
        return visitService.createVisitSlot(command);
    }

    @PatchMapping("/{visitId}/book/{patientId}")
    public VisitDto bookVisit(@PathVariable Long visitId, @PathVariable Long patientId) {
        return visitService.bookVisit(visitId, patientId);
    }

    @GetMapping("/patient/{patientId}")
    public List<VisitDto> getPatientVisits(@PathVariable Long patientId) {
        return visitService.getPatientVisits(patientId);
    }
}