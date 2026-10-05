package com.kanapa4.medical_clinic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanapa4.medical_clinic.adapter.facility.FacilityController;
import com.kanapa4.medical_clinic.domain.FacilityService;
import com.kanapa4.medical_clinic.model.FacilityCreateCommand;
import com.kanapa4.medical_clinic.model.FacilityDto;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacilityController.class)
public class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FacilityService facilityService;

    @Test
    void getPaginatedFacilities_DataCorrect_ReturnPageWithFacilityDtos() throws Exception {
        FacilityDto facility = FacilityDto.builder()
                .id(1L)
                .name("Main Clinic")
                .city("Warsaw")
                .zipCode("00-001")
                .street("Main St")
                .buildingNumber("10")
                .doctorsId(Set.of(1L))
                .build();

        Page<FacilityDto> mockPage = Page.<FacilityDto>builder()
                .content(List.of(facility))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();

        when(facilityService.getPaginatedFacilities(any(Pageable.class))).thenReturn(mockPage);

        RequestBuilder request = MockMvcRequestBuilders.get("/facilities")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Main Clinic"));
    }

    @Test
    void findById_FacilityExists_ReturnFacilityDto() throws Exception {
        FacilityDto facility = FacilityDto.builder()
                .id(1L)
                .name("Main Clinic")
                .build();
        when(facilityService.findById(1L)).thenReturn(facility);

        RequestBuilder request = MockMvcRequestBuilders.get("/facilities/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Main Clinic"));
    }

    @Test
    void create_CorrectData_ReturnCreatedFacilityDto() throws Exception {
        FacilityCreateCommand command = FacilityCreateCommand.builder()
                .name("Main Clinic")
                .city("Warsaw")
                .build();
        FacilityDto created = FacilityDto.builder()
                .id(1L)
                .name("Main Clinic")
                .city("Warsaw")
                .build();
        when(facilityService.create(any(FacilityCreateCommand.class))).thenReturn(created);

        RequestBuilder request = MockMvcRequestBuilders.post("/facilities")
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Main Clinic"));
    }

    @Test
    void update_CorrectData_ReturnUpdatedFacilityDto() throws Exception {
        FacilityDto dto = FacilityDto.builder()
                .name("Updated Clinic")
                .build();
        FacilityDto updated = FacilityDto.builder()
                .id(1L)
                .name("Updated Clinic")
                .build();
        when(facilityService.update(eq(1L), any(FacilityDto.class))).thenReturn(updated);

        RequestBuilder request = MockMvcRequestBuilders.put("/facilities/{id}", 1L)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Clinic"));
    }

    @Test
    void delete_CorrectData_ReturnNoContent() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/facilities/{id}", 1L);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}