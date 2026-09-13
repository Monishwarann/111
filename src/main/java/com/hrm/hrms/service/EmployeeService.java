package com.hrm.hrms.service;

import com.hrm.hrms.dto.EmployeeRequestDTO;
import com.hrm.hrms.dto.EmployeeResponseDTO;
import com.hrm.hrms.entity.Employee;
import com.hrm.hrms.enums.EmployeeStatus;
import com.hrm.hrms.exception.BadRequestException;
import com.hrm.hrms.exception.ResourceNotFoundException;
import com.hrm.hrms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final PayrollRepository payrollRepository;
    private final PerformanceRepository performanceRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           UserRepository userRepository,
                           AttendanceRepository attendanceRepository,
                           LeaveRepository leaveRepository,
                           PayrollRepository payrollRepository,
                           PerformanceRepository performanceRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRepository = leaveRepository;
        this.payrollRepository = payrollRepository;
        this.performanceRepository = performanceRepository;
    }

    public List<EmployeeResponseDTO> getAllEmployees(EmployeeStatus status) {
        List<Employee> employees;
        if (status != null) {
            employees = employeeRepository.findByStatus(status);
        } else {
            employees = employeeRepository.findAll();
        }
        return employees.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return mapToDTO(employee);
    }

    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto) {
        if (employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())) {
            throw new BadRequestException("Employee code already exists: " + dto.getEmployeeCode());
        }
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already exists: " + dto.getEmail());
        }
        if (dto.getSalary() < 0) {
            throw new BadRequestException("Salary cannot be negative");
        }

        Employee employee = new Employee();
        employee.setEmployeeCode(dto.getEmployeeCode());
        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setDepartment(dto.getDepartment());
        employee.setDesignation(dto.getDesignation());
        employee.setJoiningDate(dto.getJoiningDate());
        employee.setSalary(dto.getSalary());
        employee.setStatus(dto.getStatus() != null ? dto.getStatus() : EmployeeStatus.ACTIVE);

        Employee saved = employeeRepository.save(employee);
        return mapToDTO(saved);
    }

    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (!employee.getEmployeeCode().equalsIgnoreCase(dto.getEmployeeCode()) &&
                employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())) {
            throw new BadRequestException("Employee code already exists: " + dto.getEmployeeCode());
        }

        if (!employee.getEmail().equalsIgnoreCase(dto.getEmail()) &&
                employeeRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already exists: " + dto.getEmail());
        }

        if (dto.getSalary() < 0) {
            throw new BadRequestException("Salary cannot be negative");
        }

        employee.setEmployeeCode(dto.getEmployeeCode());
        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setDepartment(dto.getDepartment());
        employee.setDesignation(dto.getDesignation());
        employee.setJoiningDate(dto.getJoiningDate());
        employee.setSalary(dto.getSalary());
        if (dto.getStatus() != null) {
            employee.setStatus(dto.getStatus());
        }

        Employee updated = employeeRepository.save(employee);
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        userRepository.findByEmployeeId(id).ifPresent(u -> {
            u.setEmployee(null);
            userRepository.save(u);
        });

        attendanceRepository.deleteAll(attendanceRepository.findByEmployeeId(id));
        leaveRepository.deleteAll(leaveRepository.findByEmployeeId(id));
        payrollRepository.deleteAll(payrollRepository.findByEmployeeId(id));
        performanceRepository.deleteAll(performanceRepository.findByEmployeeId(id));

        employeeRepository.delete(employee);
    }

    public EmployeeResponseDTO mapToDTO(Employee employee) {
        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getJoiningDate(),
                employee.getSalary(),
                employee.getStatus()
        );
    }
}
