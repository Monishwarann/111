package com.hrm.hrms.config;

import com.hrm.hrms.entity.*;
import com.hrm.hrms.enums.*;
import com.hrm.hrms.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final PayrollRepository payrollRepository;
    private final PerformanceRepository performanceRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           EmployeeRepository employeeRepository,
                           AttendanceRepository attendanceRepository,
                           LeaveRepository leaveRepository,
                           PayrollRepository payrollRepository,
                           PerformanceRepository performanceRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRepository = leaveRepository;
        this.payrollRepository = payrollRepository;
        this.performanceRepository = performanceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0 || employeeRepository.count() > 0) {
            return; // Data already initialized
        }

        // 1. Seed Employees (10 employees)
        List<Employee> employees = new ArrayList<>();
        String[] departments = {"Engineering", "Human Resources", "Finance", "Marketing", "Sales"};
        String[] designations = {"Senior Software Engineer", "HR Manager", "Financial Analyst", "Marketing Executive", "Sales Lead"};

        for (int i = 1; i <= 10; i++) {
            Employee emp = new Employee();
            emp.setEmployeeCode("EMP" + String.format("%03d", i));
            emp.setName(getSampleName(i));
            emp.setEmail("employee" + i + "@company.com");
            emp.setPhone("+1-555-010" + i);
            emp.setDepartment(departments[(i - 1) % departments.length]);
            emp.setDesignation(designations[(i - 1) % designations.length]);
            emp.setJoiningDate(LocalDate.now().minusMonths(i * 3));
            emp.setSalary(60000.0 + (i * 5000.0));
            emp.setStatus(i == 10 ? EmployeeStatus.INACTIVE : EmployeeStatus.ACTIVE);
            employees.add(employeeRepository.save(emp));
        }

        // 2. Seed Users
        // Admin
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);

        // HR
        User hrUser = new User();
        hrUser.setUsername("hr");
        hrUser.setPassword(passwordEncoder.encode("hr123"));
        hrUser.setRole(Role.HR);
        hrUser.setEnabled(true);
        hrUser.setEmployee(employees.get(1)); // Linked to HR Manager
        userRepository.save(hrUser);

        // Employee
        User empUser = new User();
        empUser.setUsername("employee");
        empUser.setPassword(passwordEncoder.encode("employee123"));
        empUser.setRole(Role.EMPLOYEE);
        empUser.setEnabled(true);
        empUser.setEmployee(employees.get(0)); // Linked to EMP001
        userRepository.save(empUser);

        // 3. Seed Attendance Records (20 records)
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 10; i++) {
            Employee emp = employees.get(i);
            
            // Today's attendance
            Attendance attToday = new Attendance();
            attToday.setEmployee(emp);
            attToday.setDate(today);
            if (i % 4 == 0) {
                attToday.setCheckIn(LocalTime.of(9, 30));
                attToday.setCheckOut(LocalTime.of(17, 30));
                attToday.setStatus(AttendanceStatus.LATE);
                attToday.setWorkingHours(8.0);
            } else if (i % 7 == 0) {
                attToday.setStatus(AttendanceStatus.ABSENT);
                attToday.setWorkingHours(0.0);
            } else {
                attToday.setCheckIn(LocalTime.of(9, 0));
                attToday.setCheckOut(LocalTime.of(17, 0));
                attToday.setStatus(AttendanceStatus.PRESENT);
                attToday.setWorkingHours(8.0);
            }
            attendanceRepository.save(attToday);

            // Yesterday's attendance
            Attendance attYesterday = new Attendance();
            attYesterday.setEmployee(emp);
            attYesterday.setDate(today.minusDays(1));
            attYesterday.setCheckIn(LocalTime.of(9, 0));
            attYesterday.setCheckOut(LocalTime.of(17, 0));
            attYesterday.setStatus(AttendanceStatus.PRESENT);
            attYesterday.setWorkingHours(8.0);
            attendanceRepository.save(attYesterday);
        }

        // 4. Seed Leave Requests (5 requests)
        LeaveType[] leaveTypes = {LeaveType.CASUAL, LeaveType.SICK, LeaveType.EARNED, LeaveType.UNPAID, LeaveType.CASUAL};
        LeaveStatus[] leaveStatuses = {LeaveStatus.PENDING, LeaveStatus.APPROVED, LeaveStatus.REJECTED, LeaveStatus.PENDING, LeaveStatus.APPROVED};

        for (int i = 0; i < 5; i++) {
            LeaveRequest leave = new LeaveRequest();
            leave.setEmployee(employees.get(i));
            leave.setLeaveType(leaveTypes[i]);
            leave.setStartDate(today.plusDays(i + 2));
            leave.setEndDate(today.plusDays(i + 4));
            leave.setReason("Personal reasons / vacation request #" + (i + 1));
            leave.setStatus(leaveStatuses[i]);
            leave.setNumberOfDays(3);
            leave.setCreatedAt(LocalDateTime.now().minusDays(i));
            leaveRepository.save(leave);
        }

        // 5. Seed Payroll Records (10 records)
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();
        for (int i = 0; i < 10; i++) {
            Employee emp = employees.get(i);
            Payroll payroll = new Payroll();
            payroll.setEmployee(emp);
            payroll.setMonth(currentMonth);
            payroll.setYear(currentYear);
            payroll.setBasicSalary(emp.getSalary());
            payroll.setAllowances(5000.0);
            payroll.setDeductions(2000.0);
            payroll.setNetSalary(emp.getSalary() + 5000.0 - 2000.0);
            payroll.setPaymentStatus(i % 2 == 0 ? PaymentStatus.PAID : PaymentStatus.PENDING);
            if (payroll.getPaymentStatus() == PaymentStatus.PAID) {
                payroll.setPaymentDate(today.minusDays(5));
            }
            payrollRepository.save(payroll);
        }

        // 6. Seed Performance Reviews (10 reviews)
        double[] ratings = {4.8, 4.2, 3.8, 2.9, 4.9, 3.7, 4.5, 3.2, 4.6, 2.8};
        for (int i = 0; i < 10; i++) {
            Performance perf = new Performance();
            perf.setEmployee(employees.get(i));
            perf.setReviewDate(today.minusMonths(1));
            perf.setRating(ratings[i]);
            perf.setGoals("Complete Q3 deliverables and lead team initiative #" + (i + 1));
            perf.setFeedback("Consistently demonstrates high professional standards and leadership skills.");
            perf.setReviewer("Sarah Jenkins (HR Director)");
            performanceRepository.save(perf);
        }
    }

    private String getSampleName(int index) {
        String[] names = {
                "Alex Rivera", "Sophia Martinez", "Marcus Chen", "Emily Watson",
                "David Kim", "Jessica Taylor", "Robert Johnson", "Amanda White",
                "Daniel Vance", "Rachel Green"
        };
        return names[(index - 1) % names.length];
    }
}
