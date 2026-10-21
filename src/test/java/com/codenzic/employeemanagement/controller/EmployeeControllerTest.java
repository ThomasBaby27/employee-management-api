package com.codenzic.employeemanagement.controller;

import com.codenzic.employeemanagement.dto.EmployeeRequestDTO;
import com.codenzic.employeemanagement.dto.EmployeeResponseDTO;
import com.codenzic.employeemanagement.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/employees returns 201 Created when valid payload")
    void createEmployee_ValidPayload_Returns201() throws Exception {
        EmployeeRequestDTO request = EmployeeRequestDTO.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice.smith@example.com")
                .department("Finance")
                .designation("Analyst")
                .salary(new BigDecimal("70000.00"))
                .status("ACTIVE")
                .build();

        EmployeeResponseDTO response = EmployeeResponseDTO.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .email("alice.smith@example.com")
                .department("Finance")
                .designation("Analyst")
                .salary(new BigDecimal("70000.00"))
                .status("ACTIVE")
                .build();

        when(employeeService.createEmployee(any(EmployeeRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("alice.smith@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/employees returns 400 Bad Request when validation fails")
    void createEmployee_InvalidPayload_Returns400() throws Exception {
        EmployeeRequestDTO invalidRequest = EmployeeRequestDTO.builder()
                .firstName("") 
                .email("not-an-email") 
                .salary(new BigDecimal("-100")) 
                .build();

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    @DisplayName("GET /api/v1/employees/{id} returns 200 OK")
    void getEmployeeById_Returns200() throws Exception {
        EmployeeResponseDTO response = EmployeeResponseDTO.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .email("alice.smith@example.com")
                .department("Finance")
                .designation("Analyst")
                .salary(new BigDecimal("70000.00"))
                .status("ACTIVE")
                .build();

        when(employeeService.getEmployeeById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }
}