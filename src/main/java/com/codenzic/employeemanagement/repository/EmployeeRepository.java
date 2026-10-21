package com.codenzic.employeemanagement.repository;

import com.codenzic.employeemanagement.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Employee> findByEmail(String email);

    Page<Employee> findByDepartmentContainingIgnoreCaseAndStatusContainingIgnoreCase(
            String department,
            String status,
            Pageable pageable
    );
}