package com.hrm.hrms.controller;

import com.hrm.hrms.dto.EmployeeRequestDTO;
import com.hrm.hrms.dto.EmployeeResponseDTO;
import com.hrm.hrms.enums.EmployeeStatus;
import com.hrm.hrms.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/employees", "/employees"})
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees(
            @RequestParam(required = false) EmployeeStatus status) {
        List<EmployeeResponseDTO> employees = employeeService.getAllEmployees(status);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Long id) {
        EmployeeResponseDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@Valid @RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO created = employeeService.createEmployee(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO updated = employeeService.updateEmployee(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
