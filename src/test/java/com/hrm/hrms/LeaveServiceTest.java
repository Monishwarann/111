package com.hrm.hrms;

import com.hrm.hrms.dto.LeaveRequestDTO;
import com.hrm.hrms.dto.LeaveResponseDTO;
import com.hrm.hrms.enums.LeaveStatus;
import com.hrm.hrms.enums.LeaveType;
import com.hrm.hrms.exception.BadRequestException;
import com.hrm.hrms.service.LeaveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LeaveServiceTest {

    @Autowired
    private LeaveService leaveService;

    @Test
    void testApplyLeaveSuccess() {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setEmployeeId(1L);
        dto.setLeaveType(LeaveType.SICK);
        dto.setStartDate(LocalDate.now().plusDays(20));
        dto.setEndDate(LocalDate.now().plusDays(22));
        dto.setReason("Medical checkup");

        LeaveResponseDTO response = leaveService.applyLeave(dto);
        assertNotNull(response.getId());
        assertEquals(3, response.getNumberOfDays());
        assertEquals(LeaveStatus.PENDING, response.getStatus());
    }

    @Test
    void testInvalidDateRangeFails() {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setEmployeeId(1L);
        dto.setLeaveType(LeaveType.CASUAL);
        dto.setStartDate(LocalDate.now().plusDays(25));
        dto.setEndDate(LocalDate.now().plusDays(20)); // invalid
        dto.setReason("Invalid range");

        assertThrows(BadRequestException.class, () -> leaveService.applyLeave(dto));
    }
}
