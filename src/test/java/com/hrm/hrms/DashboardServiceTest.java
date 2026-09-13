package com.hrm.hrms;

import com.hrm.hrms.dto.DashboardSummaryDTO;
import com.hrm.hrms.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Test
    void testGetDashboardSummary() {
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary();
        assertNotNull(summary);
        assertTrue(summary.getTotalEmployees() > 0);
        assertTrue(summary.getActiveEmployees() > 0);
        assertNotNull(summary.getDepartmentDistribution());
    }
}
