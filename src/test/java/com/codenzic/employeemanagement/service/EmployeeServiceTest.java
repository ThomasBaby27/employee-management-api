package com.codenzic.employeemanagement.service;

import com.codenzic.employeemanagement.dto.EmployeeRequestDTO;
import com.codenzic.employeemanagement.dto.EmployeeResponseDTO;
import com.codenzic.employeemanagement.entity.Employee;
import com.codenzic.employeemanagement.exception.DuplicateResourceException;
import com.codenzic.employeemanagement.exception.ResourceNotFoundException;
import com.codenzic.employeemanagement.repository.EmployeeRepository;
import com.codenzic.employeemanagement.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department("Engineering")
                .designation("Backend Developer")
                .salary(new BigDecimal("80000.00"))
                .status("ACTIVE")
                .build();

        requestDTO = EmployeeRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department("Engineering")
                .designation("Backend Developer")
                .salary(new BigDecimal("80000.00"))
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Should successfully create an employee")
    void createEmployee_Success() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponseDTO result = employeeService.createEmployee(requestDTO);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("Engineering", result.getDepartment());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void createEmployee_DuplicateEmail_ThrowsException() {
        when(employeeRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> employeeService.createEmployee(requestDTO));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should find employee by ID")
    void getEmployeeById_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponseDTO result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when employee ID does not exist")
    void getEmployeeById_NotFound_ThrowsException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(99L));
    }

    @Test
    @DisplayName("Should return paginated employees")
    void getAllEmployees_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> pagedEmployees = new PageImpl<>(List.of(employee));

        when(employeeRepository.findByDepartmentContainingIgnoreCaseAndStatusContainingIgnoreCase(
                "", "", pageable)).thenReturn(pagedEmployees);

        Page<EmployeeResponseDTO> result = employeeService.getAllEmployees(null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("john.doe@example.com", result.getContent().get(0).getEmail());
    }

    @Test
    @DisplayName("Should delete employee when ID exists")
    void deleteEmployee_Success() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(1L);

        assertDoesNotThrow(() -> employeeService.deleteEmployee(1L));
        verify(employeeRepository, times(1)).deleteById(1L);
    }
}