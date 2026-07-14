package com.kanapa4.medical_clinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanapa4.medical_clinic.model.dto.VisitCreateCommand;
import com.kanapa4.medical_clinic.model.dto.VisitDto;
import com.kanapa4.medical_clinic.service.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitController.class)
public class VisitControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private VisitService visitService;

    @Test
    void getPaginatedVisits_DataCorrect_ReturnPageWithVisitDtos() throws Exception {
        VisitDto visit = VisitDto.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 7, 14, 12, 0))
                .durationInMinutes(30)
                .doctorId(1L)
                .patientId(2L)
                .facilityId(3L)
                .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<VisitDto> mockPage = new PageImpl<>(List.of(visit), pageable, 1);
        when(visitService.getPaginatedVisits(0, 10, "id")).thenReturn(mockPage);

        RequestBuilder request = MockMvcRequestBuilders.get("/visits")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "id")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].durationInMinutes").value(30));
    }

    @Test
    void createVisitSlot_CorrectData_ReturnCreatedVisitDto() throws Exception {
        VisitCreateCommand command = VisitCreateCommand.builder()
                .dateTime(LocalDateTime.of(2026, 7, 14, 12, 0))
                .durationInMinutes(30)
                .doctorId(1L)
                .facilityId(3L)
                .build();
        VisitDto created = VisitDto.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 7, 14, 12, 0))
                .durationInMinutes(30)
                .doctorId(1L)
                .facilityId(3L)
                .build();
        when(visitService.createVisitSlot(any(VisitCreateCommand.class))).thenReturn(created);

        RequestBuilder request = MockMvcRequestBuilders.post("/visits")
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.durationInMinutes").value(30));
    }

    @Test
    void update_CorrectData_ReturnUpdatedVisitDto() throws Exception {
        VisitCreateCommand command = VisitCreateCommand.builder()
                .dateTime(LocalDateTime.of(2026, 7, 14, 13, 0))
                .durationInMinutes(45)
                .build();
        VisitDto updated = VisitDto.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 7, 14, 13, 0))
                .durationInMinutes(45)
                .build();
        when(visitService.updateVisit(eq(1L), any(VisitCreateCommand.class))).thenReturn(updated);

        RequestBuilder request = MockMvcRequestBuilders.put("/visits/{id}", 1L)
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.durationInMinutes").value(45));
    }

    @Test
    void delete_CorrectData_ReturnOk() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/visits/{id}", 1L);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void bookVisit_CorrectData_ReturnUpdatedVisitDto() throws Exception {
        VisitDto booked = VisitDto.builder()
                .id(1L)
                .patientId(2L)
                .build();
        when(visitService.bookVisit(1L, 2L)).thenReturn(booked);

        RequestBuilder request = MockMvcRequestBuilders.patch("/visits/{visitId}/book/{patientId}", 1L, 2L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(2));
    }

    @Test
    void getPatientVisits_CorrectData_ReturnList() throws Exception {
        VisitDto visit = VisitDto.builder()
                .id(1L)
                .patientId(2L)
                .build();
        when(visitService.getPatientVisits(2L)).thenReturn(List.of(visit));

        RequestBuilder request = MockMvcRequestBuilders.get("/visits/patient/{patientId}", 2L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
