package com.hrm.hrms;

import com.hrm.hrms.dto.AttendanceRequestDTO;
import com.hrm.hrms.exception.BadRequestException;
import com.hrm.hrms.service.AttendanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AttendanceServiceTest {

    @Autowired
    private AttendanceService attendanceService;

    @Test
    void testWorkingHoursCalculation() {
        Double hours = attendanceService.calculateWorkingHours(LocalTime.of(9, 0), LocalTime.of(17, 30));
        assertEquals(8.5, hours);
    }

    @Test
    void testInvalidCheckOutFails() {
        AttendanceRequestDTO dto = new AttendanceRequestDTO();
        dto.setEmployeeId(1L);
        dto.setDate(LocalDate.now().plusDays(10));
        dto.setCheckIn(LocalTime.of(17, 0));
        dto.setCheckOut(LocalTime.of(9, 0));

        assertThrows(BadRequestException.class, () -> attendanceService.markAttendance(dto));
    }

    @Test
    void testDuplicateAttendanceFails() {
        AttendanceRequestDTO dto = new AttendanceRequestDTO();
        dto.setEmployeeId(1L);
        dto.setDate(LocalDate.now()); // Already seeded in DataInitializer
        dto.setCheckIn(LocalTime.of(9, 0));
        dto.setCheckOut(LocalTime.of(17, 0));

        assertThrows(BadRequestException.class, () -> attendanceService.markAttendance(dto));
    }
}
