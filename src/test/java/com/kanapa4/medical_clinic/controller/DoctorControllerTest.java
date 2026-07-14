package com.kanapa4.medical_clinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanapa4.medical_clinic.model.Specialization;
import com.kanapa4.medical_clinic.model.dto.DoctorCreateCommand;
import com.kanapa4.medical_clinic.model.dto.DoctorDto;
import com.kanapa4.medical_clinic.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
public class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private DoctorService doctorService;

    @Test
    void getPaginatedDoctors_DataCorrect_ReturnPageWithDoctorDtos() throws Exception {
        DoctorDto doctor = DoctorDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .specialization(Specialization.CARDIOLOGY)
                .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<DoctorDto> mockPage = new PageImpl<>(List.of(doctor), pageable, 1);
        when(doctorService.getPaginatedDoctors(0, 10, "id")).thenReturn(mockPage);

        RequestBuilder request = MockMvcRequestBuilders.get("/doctors")
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
                .andExpect(jsonPath("$.content[0].firstName").value("John"));
    }

    @Test
    void findById_DoctorExists_ReturnDoctorDto() throws Exception {
        DoctorDto doctor = DoctorDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .build();
        when(doctorService.findById(1L)).thenReturn(doctor);

        RequestBuilder request = MockMvcRequestBuilders.get("/doctors/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void add_CorrectData_ReturnCreatedDoctorDto() throws Exception {
        DoctorCreateCommand command = DoctorCreateCommand.builder()
                .firstName("John")
                .lastName("Smith")
                .specialization(Specialization.CARDIOLOGY)
                .userId(2L)
                .build();
        DoctorDto created = DoctorDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .specialization(Specialization.CARDIOLOGY)
                .build();
        when(doctorService.create(any(DoctorCreateCommand.class))).thenReturn(created);

        RequestBuilder request = MockMvcRequestBuilders.post("/doctors")
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void update_CorrectData_ReturnUpdatedDoctorDto() throws Exception {
        DoctorDto dto = DoctorDto.builder()
                .firstName("John")
                .lastName("Updated")
                .build();
        DoctorDto updated = DoctorDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Updated")
                .build();
        when(doctorService.update(eq(1L), any(DoctorDto.class))).thenReturn(updated);

        RequestBuilder request = MockMvcRequestBuilders.put("/doctors/{id}", 1L)
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
        RequestBuilder request = MockMvcRequestBuilders.delete("/doctors/{id}", 1L);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void addFacilityToDoctor_CorrectData_ReturnDoctorDto() throws Exception {
        DoctorDto updated = DoctorDto.builder()
                .id(1L)
                .firstName("John")
                .build();
        when(doctorService.addFacilityToDoctor(1L, 2L)).thenReturn(updated);

        RequestBuilder request = MockMvcRequestBuilders.post("/doctors/{doctorId}/facilities/{facilityId}", 1L, 2L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void removeFacilityFromDoctor_CorrectData_ReturnNoContent() throws Exception {
        DoctorDto updated = DoctorDto.builder()
                .id(1L)
                .firstName("John")
                .build();
        when(doctorService.removeFacilityFromDoctor(1L, 2L)).thenReturn(updated);

        RequestBuilder request = MockMvcRequestBuilders.delete("/doctors/{doctorId}/facilities/{facilityId}", 1L, 2L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
