package com.hrm.hrms.controller;

import com.hrm.hrms.dto.*;
import com.hrm.hrms.enums.*;
import com.hrm.hrms.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping({"/api/reports", "/reports"})
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeeReport(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        return ResponseEntity.ok(reportService.getEmployeeReport(department, designation, status, dateFrom, dateTo));
    }

    @GetMapping("/attendance")
    public ResponseEntity<List<AttendanceResponseDTO>> getAttendanceReport(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) AttendanceStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        return ResponseEntity.ok(reportService.getAttendanceReport(employeeId, department, status, dateFrom, dateTo));
    }

    @GetMapping("/leaves")
    public ResponseEntity<List<LeaveResponseDTO>> getLeaveReport(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) LeaveType leaveType,
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        return ResponseEntity.ok(reportService.getLeaveReport(employeeId, leaveType, status, dateFrom, dateTo));
    }

    @GetMapping("/payroll")
    public ResponseEntity<List<PayrollResponseDTO>> getPayrollReport(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) PaymentStatus paymentStatus) {
        return ResponseEntity.ok(reportService.getPayrollReport(employeeId, month, year, paymentStatus));
    }

    @GetMapping("/performance")
    public ResponseEntity<List<PerformanceResponseDTO>> getPerformanceReport(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) String reviewer,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        return ResponseEntity.ok(reportService.getPerformanceReport(employeeId, rating, reviewer, dateFrom, dateTo));
    }
}
