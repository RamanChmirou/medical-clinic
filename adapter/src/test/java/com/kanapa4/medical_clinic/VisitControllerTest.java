package com.kanapa4.medical_clinic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanapa4.medical_clinic.adapter.visit.VisitController;
import com.kanapa4.medical_clinic.domain.VisitService;
import com.kanapa4.medical_clinic.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
    void getVisits_DataCorrect_ReturnPageWithVisitDtos() throws Exception {
        VisitDto visit = VisitDto.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 7, 14, 12, 0))
                .durationInMinutes(30)
                .doctorId(1L)
                .patientId(2L)
                .facilityId(3L)
                .build();

        Page<VisitDto> mockPage = Page.<VisitDto>builder()
                .content(List.of(visit))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();

        when(visitService.searchVisits(any(VisitFilter.class), any(Pageable.class))).thenReturn(mockPage);

        RequestBuilder request = MockMvcRequestBuilders.get("/visits")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.page").value(0))
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
    void delete_CorrectData_ReturnNoContent() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/visits/{id}", 1L);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        verify(visitService).delete(1L);
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
    void cancelVisit_CorrectData_ReturnUpdatedVisitDto() throws Exception {
        VisitDto cancelled = VisitDto.builder()
                .id(1L)
                .patientId(null)
                .build();

        when(visitService.cancelVisit(1L)).thenReturn(cancelled);

        RequestBuilder request = MockMvcRequestBuilders.patch("/visits/{visitId}/cancel", 1L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").doesNotExist());
    }
}