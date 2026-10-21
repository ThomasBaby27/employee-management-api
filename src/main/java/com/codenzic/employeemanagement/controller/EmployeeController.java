package com.codenzic.employeemanagement.controller;

import com.codenzic.employeemanagement.dto.EmployeeRequestDTO;
import com.codenzic.employeemanagement.dto.EmployeeResponseDTO;
import com.codenzic.employeemanagement.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "APIs for creating, reading, updating, and deleting employee records")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "Create a new employee", description = "Validates input and persists a new employee record in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation constraint failure"),
            @ApiResponse(responseCode = "409", description = "Duplicate employee email detected")
    })
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@Valid @RequestBody EmployeeRequestDTO requestDTO) {
        EmployeeResponseDTO created = employeeService.createEmployee(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all employees", description = "Retrieves a paginated and sortable list of employees with optional department and status filters")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved employee list")
    public ResponseEntity<Page<EmployeeResponseDTO>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<EmployeeResponseDTO> employees = employeeService.getAllEmployees(department, status, pageable);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID", description = "Fetches a single employee by their unique primary key identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "404", description = "Employee not found with provided ID")
    })
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Long id) {
        EmployeeResponseDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing employee", description = "Updates details of an existing employee identified by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation failure"),
            @ApiResponse(responseCode = "404", description = "Employee not found with provided ID"),
            @ApiResponse(responseCode = "409", description = "Email is already taken by another employee")
    })
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO requestDTO
    ) {
        EmployeeResponseDTO updated = employeeService.updateEmployee(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee", description = "Removes an employee record from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully (no content)"),
            @ApiResponse(responseCode = "404", description = "Employee not found with provided ID")
    })
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}