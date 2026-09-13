package com.hrm.hrms;

import com.hrm.hrms.dto.PerformanceRequestDTO;
import com.hrm.hrms.exception.BadRequestException;
import com.hrm.hrms.service.PerformanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PerformanceServiceTest {

    @Autowired
    private PerformanceService performanceService;

    @Test
    void testRatingClassification() {
        assertEquals("Excellent", performanceService.classifyRating(4.8));
        assertEquals("Good", performanceService.classifyRating(4.0));
        assertEquals("Average", performanceService.classifyRating(3.0));
        assertEquals("Needs Improvement", performanceService.classifyRating(2.0));
    }

    @Test
    void testInvalidRatingFails() {
        PerformanceRequestDTO dto = new PerformanceRequestDTO();
        dto.setEmployeeId(1L);
        dto.setReviewDate(LocalDate.now());
        dto.setRating(6.5); // Invalid
        dto.setReviewer("HR Manager");

        assertThrows(BadRequestException.class, () -> performanceService.createPerformance(dto));
    }
}
