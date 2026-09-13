package com.hrm.hrms;

import com.hrm.hrms.dto.EmployeeRequestDTO;
import com.hrm.hrms.dto.EmployeeResponseDTO;
import com.hrm.hrms.enums.EmployeeStatus;
import com.hrm.hrms.exception.BadRequestException;
import com.hrm.hrms.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Test
    void testCreateEmployeeSuccess() {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setEmployeeCode("TEST001");
        dto.setName("John Test");
        dto.setEmail("johntest@company.com");
        dto.setPhone("+1-555-9999");
        dto.setDepartment("Engineering");
        dto.setDesignation("Developer");
        dto.setJoiningDate(LocalDate.now());
        dto.setSalary(75000.0);
        dto.setStatus(EmployeeStatus.ACTIVE);

        EmployeeResponseDTO response = employeeService.createEmployee(dto);

        assertNotNull(response.getId());
        assertEquals("TEST001", response.getEmployeeCode());
        assertEquals("johntest@company.com", response.getEmail());
    }

    @Test
    void testCreateDuplicateEmailFails() {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setEmployeeCode("TEST002");
        dto.setName("John Duplicate");
        dto.setEmail("employee1@company.com"); // already seeded
        dto.setPhone("+1-555-9999");
        dto.setDepartment("Engineering");
        dto.setDesignation("Developer");
        dto.setJoiningDate(LocalDate.now());
        dto.setSalary(75000.0);

        assertThrows(BadRequestException.class, () -> employeeService.createEmployee(dto));
    }

    @Test
    void testNegativeSalaryFails() {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setEmployeeCode("TEST003");
        dto.setName("John Invalid");
        dto.setEmail("invalid@company.com");
        dto.setPhone("+1-555-9999");
        dto.setDepartment("Engineering");
        dto.setDesignation("Developer");
        dto.setJoiningDate(LocalDate.now());
        dto.setSalary(-500.0);

        assertThrows(BadRequestException.class, () -> employeeService.createEmployee(dto));
    }

    @Test
    void testGetAllEmployees() {
        List<EmployeeResponseDTO> employees = employeeService.getAllEmployees(null);
        assertFalse(employees.isEmpty());
    }
}
