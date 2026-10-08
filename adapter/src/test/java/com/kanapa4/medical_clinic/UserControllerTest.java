package com.kanapa4.medical_clinic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanapa4.medical_clinic.adapter.user.UserController;
import com.kanapa4.medical_clinic.domain.UserService;
import com.kanapa4.medical_clinic.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void getPaginatedUsers_DataCorrect_ReturnPageWithUserDtos() throws Exception {
        // given
        UserDto user = UserDto.builder()
                .id(1L)
                .email("doctor.smith@clinic.com")
                .build();

        Page<UserDto> mockPage = Page.<UserDto>builder()
                .content(List.of(user))
                .page(0)
                .size(20)
                .totalElements(1L)
                .totalPages(1)
                .build();

        when(userService.getPaginatedUsers(any(Pageable.class))).thenReturn(mockPage);

        // when & then
        RequestBuilder request = MockMvcRequestBuilders.get("/users")
                .param("page", "0")
                .param("size", "20")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].email").value("doctor.smith@clinic.com"));
    }

    @Test
    void create_CorrectData_ReturnCreatedUserDto() throws Exception {
        // given
        UserCreateCommand userCreateCommand = UserCreateCommand.builder()
                .email("email@")
                .build();

        UserDto userDto = UserDto.builder()
                .email("email@")
                .build();
        when(userService.create(any(UserCreateCommand.class))).thenReturn(userDto);

        // when & then
        RequestBuilder request = MockMvcRequestBuilders.post("/users")
                .content(objectMapper.writeValueAsString(userCreateCommand))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("email@"));
    }

    @Test
    void update_DataCorrect_ReturnUpdatedUserDto() throws Exception {
        // given
        String email = "email@com";
        UserDto userDto = UserDto.builder()
                .email("email@com")
                .build();
        UserDto updatedUser = UserDto.builder()
                .email("email@com")
                .build();
        when(userService.update(eq(email), any(UserDto.class))).thenReturn(updatedUser);

        // when & then
        RequestBuilder request = MockMvcRequestBuilders.put("/users/{email}", email)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("email@com"));
    }

    @Test
    void delete_CorrectData_ReturnNoContent() throws Exception {
        // given
        String email = "email@com";

        // when & then
        RequestBuilder request = MockMvcRequestBuilders.delete("/users/{email}", email);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void editPassword_CorrectData_ReturnOk() throws Exception {
        // given
        String email = "email@com";
        EditPasswordCommand command = new EditPasswordCommand("new_password");

        // when & then
        RequestBuilder request = MockMvcRequestBuilders.patch("/users/{email}", email)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }
}
