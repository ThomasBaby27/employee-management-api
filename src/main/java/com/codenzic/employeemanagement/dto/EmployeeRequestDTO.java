package com.codenzic.employeemanagement.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequestDTO {

    @NotBlank(message = "First name is mandatory")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be a well-formed email address")
    private String email;

    @NotBlank(message = "Department is mandatory")
    private String department;

    @NotBlank(message = "Designation is mandatory")
    private String designation;

    @NotNull(message = "Salary is mandatory")
    @Positive(message = "Salary must be greater than zero")
    private BigDecimal salary;

    @NotBlank(message = "Status is mandatory (e.g. ACTIVE, INACTIVE)")
    private String status;
}