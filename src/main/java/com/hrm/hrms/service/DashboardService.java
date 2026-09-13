package com.hrm.hrms.service;

import com.hrm.hrms.dto.DashboardSummaryDTO;
import com.hrm.hrms.entity.Employee;
import com.hrm.hrms.enums.AttendanceStatus;
import com.hrm.hrms.enums.EmployeeStatus;
import com.hrm.hrms.enums.LeaveStatus;
import com.hrm.hrms.enums.PaymentStatus;
import com.hrm.hrms.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final PayrollRepository payrollRepository;
    private final PerformanceRepository performanceRepository;

    private final EmployeeService employeeService;
    private final LeaveService leaveService;
    private final PayrollService payrollService;

    public DashboardService(EmployeeRepository employeeRepository,
                            AttendanceRepository attendanceRepository,
                            LeaveRepository leaveRepository,
                            PayrollRepository payrollRepository,
                            PerformanceRepository performanceRepository,
                            EmployeeService employeeService,
                            LeaveService leaveService,
                            PayrollService payrollService) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRepository = leaveRepository;
        this.payrollRepository = payrollRepository;
        this.performanceRepository = performanceRepository;
        this.employeeService = employeeService;
        this.leaveService = leaveService;
        this.payrollService = payrollService;
    }

    public DashboardSummaryDTO getDashboardSummary() {
        DashboardSummaryDTO summary = new DashboardSummaryDTO();

        long totalEmployees = employeeRepository.count();
        long activeEmployees = employeeRepository.countByStatus(EmployeeStatus.ACTIVE);

        LocalDate today = LocalDate.now();
        long presentToday = attendanceRepository.countByDateAndStatus(today, AttendanceStatus.PRESENT);
        long lateToday = attendanceRepository.countByDateAndStatus(today, AttendanceStatus.LATE);
        long absentToday = attendanceRepository.countByDateAndStatus(today, AttendanceStatus.ABSENT);

        long pendingLeaves = leaveRepository.countByStatus(LeaveStatus.PENDING);
        long pendingPayroll = payrollRepository.countByPaymentStatus(PaymentStatus.PENDING);

        Double avgRatingObj = performanceRepository.findAverageRatingOverall();
        double averageRating = avgRatingObj != null ? Math.round(avgRatingObj * 10.0) / 10.0 : 0.0;

        double attendancePercentage = 0.0;
        if (activeEmployees > 0) {
            long presentOrLate = presentToday + lateToday;
            attendancePercentage = Math.round((double) presentOrLate / activeEmployees * 1000.0) / 10.0;
        }

        Map<String, Long> departmentDist = employeeRepository.findAll().stream()
                .filter(e -> e.getDepartment() != null && !e.getDepartment().isBlank())
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));

        summary.setTotalEmployees(totalEmployees);
        summary.setActiveEmployees(activeEmployees);
        summary.setPresentToday(presentToday);
        summary.setAbsentToday(absentToday);
        summary.setLateToday(lateToday);
        summary.setPendingLeaves(pendingLeaves);
        summary.setPendingPayroll(pendingPayroll);
        summary.setAverageRating(averageRating);
        summary.setAttendancePercentage(attendancePercentage);
        summary.setDepartmentDistribution(departmentDist);

        summary.setRecentEmployees(employeeService.getAllEmployees(null).stream().limit(5).collect(Collectors.toList()));
        summary.setRecentLeaves(leaveService.getAllLeaves().stream().limit(5).collect(Collectors.toList()));
        summary.setRecentPayroll(payrollService.getAllPayroll().stream().limit(5).collect(Collectors.toList()));

        return summary;
    }
}
