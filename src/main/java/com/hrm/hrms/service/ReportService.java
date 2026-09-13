package com.hrm.hrms.service;

import com.hrm.hrms.dto.*;
import com.hrm.hrms.entity.*;
import com.hrm.hrms.enums.*;
import com.hrm.hrms.repository.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final PayrollRepository payrollRepository;
    private final PerformanceRepository performanceRepository;

    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final LeaveService leaveService;
    private final PayrollService payrollService;
    private final PerformanceService performanceService;

    public ReportService(EmployeeRepository employeeRepository,
                         AttendanceRepository attendanceRepository,
                         LeaveRepository leaveRepository,
                         PayrollRepository payrollRepository,
                         PerformanceRepository performanceRepository,
                         EmployeeService employeeService,
                         AttendanceService attendanceService,
                         LeaveService leaveService,
                         PayrollService payrollService,
                         PerformanceService performanceService) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRepository = leaveRepository;
        this.payrollRepository = payrollRepository;
        this.performanceRepository = performanceRepository;
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
        this.leaveService = leaveService;
        this.payrollService = payrollService;
        this.performanceService = performanceService;
    }

    public List<EmployeeResponseDTO> getEmployeeReport(String department, String designation, EmployeeStatus status, LocalDate dateFrom, LocalDate dateTo) {
        Specification<Employee> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (department != null && !department.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("department")), department.toLowerCase()));
            }
            if (designation != null && !designation.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("designation")), designation.toLowerCase()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("joiningDate"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("joiningDate"), dateTo));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return employeeRepository.findAll(spec).stream()
                .map(employeeService::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AttendanceResponseDTO> getAttendanceReport(Long employeeId, String department, AttendanceStatus status, LocalDate dateFrom, LocalDate dateTo) {
        Specification<Attendance> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (employeeId != null) {
                predicates.add(cb.equal(root.get("employee").get("id"), employeeId));
            }
            if (department != null && !department.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("employee").get("department")), department.toLowerCase()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), dateTo));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return attendanceRepository.findAll(spec).stream()
                .map(attendanceService::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveResponseDTO> getLeaveReport(Long employeeId, LeaveType leaveType, LeaveStatus status, LocalDate dateFrom, LocalDate dateTo) {
        Specification<LeaveRequest> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (employeeId != null) {
                predicates.add(cb.equal(root.get("employee").get("id"), employeeId));
            }
            if (leaveType != null) {
                predicates.add(cb.equal(root.get("leaveType"), leaveType));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), dateTo));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return leaveRepository.findAll(spec).stream()
                .map(leaveService::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PayrollResponseDTO> getPayrollReport(Long employeeId, Integer month, Integer year, PaymentStatus paymentStatus) {
        Specification<Payroll> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (employeeId != null) {
                predicates.add(cb.equal(root.get("employee").get("id"), employeeId));
            }
            if (month != null) {
                predicates.add(cb.equal(root.get("month"), month));
            }
            if (year != null) {
                predicates.add(cb.equal(root.get("year"), year));
            }
            if (paymentStatus != null) {
                predicates.add(cb.equal(root.get("paymentStatus"), paymentStatus));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return payrollRepository.findAll(spec).stream()
                .map(payrollService::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PerformanceResponseDTO> getPerformanceReport(Long employeeId, Double rating, String reviewer, LocalDate dateFrom, LocalDate dateTo) {
        Specification<Performance> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (employeeId != null) {
                predicates.add(cb.equal(root.get("employee").get("id"), employeeId));
            }
            if (rating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), rating));
            }
            if (reviewer != null && !reviewer.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("reviewer")), reviewer.toLowerCase()));
            }
            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("reviewDate"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("reviewDate"), dateTo));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return performanceRepository.findAll(spec).stream()
                .map(performanceService::mapToDTO)
                .collect(Collectors.toList());
    }
}
