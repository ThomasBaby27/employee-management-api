package com.codenzic.employeemanagement.service.impl;

import com.codenzic.employeemanagement.dto.EmployeeRequestDTO;
import com.codenzic.employeemanagement.dto.EmployeeResponseDTO;
import com.codenzic.employeemanagement.entity.Employee;
import com.codenzic.employeemanagement.exception.DuplicateResourceException;
import com.codenzic.employeemanagement.exception.ResourceNotFoundException;
import com.codenzic.employeemanagement.repository.EmployeeRepository;
import com.codenzic.employeemanagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO) {
        if (employeeRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Employee with email " + requestDTO.getEmail() + " already exists");
        }

        Employee employee = mapToEntity(requestDTO);
        Employee savedEmployee = employeeRepository.save(employee);
        return mapToDTO(savedEmployee);
    }

    @Override
    public Page<EmployeeResponseDTO> getAllEmployees(String department, String status, Pageable pageable) {
        String deptFilter = (department == null) ? "" : department.trim();
        String statusFilter = (status == null) ? "" : status.trim();

        return employeeRepository
                .findByDepartmentContainingIgnoreCaseAndStatusContainingIgnoreCase(deptFilter, statusFilter, pageable)
                .map(this::mapToDTO);
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return mapToDTO(employee);
    }

    @Override
    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDTO) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (employeeRepository.existsByEmailAndIdNot(requestDTO.getEmail(), id)) {
            throw new DuplicateResourceException("Email " + requestDTO.getEmail() + " is already in use by another employee");
        }

        existingEmployee.setFirstName(requestDTO.getFirstName().trim());
        existingEmployee.setLastName(requestDTO.getLastName().trim());
        existingEmployee.setEmail(requestDTO.getEmail().trim());
        existingEmployee.setDepartment(requestDTO.getDepartment().trim());
        existingEmployee.setDesignation(requestDTO.getDesignation().trim());
        existingEmployee.setSalary(requestDTO.getSalary());
        existingEmployee.setStatus(requestDTO.getStatus().trim());

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return mapToDTO(updatedEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    private Employee mapToEntity(EmployeeRequestDTO dto) {
        return Employee.builder()
                .firstName(dto.getFirstName().trim())
                .lastName(dto.getLastName().trim())
                .email(dto.getEmail().trim())
                .department(dto.getDepartment().trim())
                .designation(dto.getDesignation().trim())
                .salary(dto.getSalary())
                .status(dto.getStatus().trim())
                .build();
    }

    private EmployeeResponseDTO mapToDTO(Employee entity) {
        return EmployeeResponseDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .department(entity.getDepartment())
                .designation(entity.getDesignation())
                .salary(entity.getSalary())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}