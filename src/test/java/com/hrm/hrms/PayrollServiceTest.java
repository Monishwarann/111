package com.hrm.hrms;

import com.hrm.hrms.dto.PayrollRequestDTO;
import com.hrm.hrms.exception.BadRequestException;
import com.hrm.hrms.service.PayrollService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PayrollServiceTest {

    @Autowired
    private PayrollService payrollService;

    @Test
    void testNetSalaryCalculation() {
        double net = payrollService.calculateNetSalary(50000.0, 3000.0, 1500.0);
        assertEquals(51500.0, net);
    }

    @Test
    void testCreateDuplicatePayrollFails() {
        PayrollRequestDTO dto = new PayrollRequestDTO();
        dto.setEmployeeId(1L);
        dto.setMonth(LocalDate.now().getMonthValue());
        dto.setYear(LocalDate.now().getYear());
        dto.setBasicSalary(60000.0);
        dto.setAllowances(1000.0);
        dto.setDeductions(500.0);

        assertThrows(BadRequestException.class, () -> payrollService.createPayroll(dto));
    }
}
