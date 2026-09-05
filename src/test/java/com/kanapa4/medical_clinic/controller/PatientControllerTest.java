package com.kanapa4.medical_clinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanapa4.medical_clinic.model.dto.PatientCreateCommand;
import com.kanapa4.medical_clinic.model.dto.PatientDto;
import com.kanapa4.medical_clinic.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
public class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private PatientService patientService;

    @Test
    void getPaginatedPatients_DataCorrect_ReturnPageWithPatientDtos() throws Exception {
        PatientDto patient = PatientDto.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Green")
                .phoneNumber("123456789")
                .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<PatientDto> mockPage = new PageImpl<>(List.of(patient), pageable, 1);
        when(patientService.getPaginatedPatients(0, 10, "id")).thenReturn(mockPage);

        RequestBuilder request = MockMvcRequestBuilders.get("/patients")
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
                .andExpect(jsonPath("$.content[0].firstName").value("Alice"));
    }

    @Test
    void findByIdCardNo_PatientExists_ReturnPatientDto() throws Exception {
        PatientDto patient = PatientDto.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Green")
                .build();
        when(patientService.findById(1L)).thenReturn(patient);

        RequestBuilder request = MockMvcRequestBuilders.get("/patients/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void add_CorrectData_ReturnCreatedPatientDto() throws Exception {
        PatientCreateCommand command = PatientCreateCommand.builder()
                .firstName("Alice")
                .lastName("Green")
                .idCardNo("ABC123456")
                .birthday(LocalDate.of(1990, 1, 1))
                .phoneNumber("123456789")
                .userId(1L)
                .build();
        PatientDto created = PatientDto.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Green")
                .phoneNumber("123456789")
                .build();
        when(patientService.create(any(PatientCreateCommand.class))).thenReturn(created);

        RequestBuilder request = MockMvcRequestBuilders.post("/patients")
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void addToUserAccount_CorrectData_ReturnCreatedPatientDto() throws Exception {
        String email = "user@test.com";
        PatientCreateCommand command = PatientCreateCommand.builder()
                .firstName("Alice")
                .lastName("Green")
                .idCardNo("ABC123456")
                .birthday(LocalDate.of(1990, 1, 1))
                .phoneNumber("123456789")
                .build();
        PatientDto created = PatientDto.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Green")
                .phoneNumber("123456789")
                .build();
        when(patientService.createPatientForUser(eq(email), any(PatientCreateCommand.class))).thenReturn(created);

        RequestBuilder request = MockMvcRequestBuilders.post("/patients/user/{email}", email)
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void update_CorrectData_ReturnUpdatedPatientDto() throws Exception {
        PatientDto dto = PatientDto.builder()
                .firstName("Alice")
                .lastName("Updated")
                .build();
        PatientDto updated = PatientDto.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Updated")
                .build();
        when(patientService.update(eq(1L), any(PatientDto.class))).thenReturn(updated);

        RequestBuilder request = MockMvcRequestBuilders.put("/patients/{id}", 1L)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lastName").value("Updated"));
    }

    @Test
    void delete_CorrectData_ReturnNoContent() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/patients/{id}", 1L);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
