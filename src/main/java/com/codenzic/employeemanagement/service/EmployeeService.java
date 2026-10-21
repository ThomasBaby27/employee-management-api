package com.codenzic.employeemanagement.service;

import com.codenzic.employeemanagement.dto.EmployeeRequestDTO;
import com.codenzic.employeemanagement.dto.EmployeeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO);

    Page<EmployeeResponseDTO> getAllEmployees(String department, String status, Pageable pageable);

    EmployeeResponseDTO getEmployeeById(Long id);

    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDTO);

    void deleteEmployee(Long id);
}