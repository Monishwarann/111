package com.hrm.hrms.service;

import com.hrm.hrms.dto.LeaveRequestDTO;
import com.hrm.hrms.dto.LeaveResponseDTO;
import com.hrm.hrms.entity.Employee;
import com.hrm.hrms.entity.LeaveRequest;
import com.hrm.hrms.enums.LeaveStatus;
import com.hrm.hrms.exception.BadRequestException;
import com.hrm.hrms.exception.ResourceNotFoundException;
import com.hrm.hrms.repository.EmployeeRepository;
import com.hrm.hrms.repository.LeaveRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveService(LeaveRepository leaveRepository, EmployeeRepository employeeRepository) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<LeaveResponseDTO> getAllLeaves() {
        return leaveRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public LeaveResponseDTO getLeaveById(Long id) {
        LeaveRequest leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        return mapToDTO(leave);
    }

    public List<LeaveResponseDTO> getLeavesByEmployeeId(Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveResponseDTO> getLeavesByStatus(LeaveStatus status) {
        return leaveRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public LeaveResponseDTO applyLeave(LeaveRequestDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date");
        }

        List<LeaveRequest> overlapping = leaveRepository.findOverlappingLeaves(dto.getEmployeeId(), dto.getStartDate(), dto.getEndDate());
        if (!overlapping.isEmpty()) {
            throw new BadRequestException("Leave request overlaps with existing leave application");
        }

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setLeaveType(dto.getLeaveType());
        leave.setStartDate(dto.getStartDate());
        leave.setEndDate(dto.getEndDate());
        leave.setReason(dto.getReason());
        leave.setStatus(LeaveStatus.PENDING);
        
        int days = (int) ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
        leave.setNumberOfDays(days);
        leave.setCreatedAt(LocalDateTime.now());

        LeaveRequest saved = leaveRepository.save(leave);
        return mapToDTO(saved);
    }

    public LeaveResponseDTO approveLeave(Long id) {
        LeaveRequest leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Only PENDING leave requests can be approved");
        }

        leave.setStatus(LeaveStatus.APPROVED);
        LeaveRequest updated = leaveRepository.save(leave);
        return mapToDTO(updated);
    }

    public LeaveResponseDTO rejectLeave(Long id) {
        LeaveRequest leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Only PENDING leave requests can be rejected");
        }

        leave.setStatus(LeaveStatus.REJECTED);
        LeaveRequest updated = leaveRepository.save(leave);
        return mapToDTO(updated);
    }

    public void deleteLeave(Long id) {
        if (!leaveRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leave request not found with id: " + id);
        }
        leaveRepository.deleteById(id);
    }

    public LeaveResponseDTO mapToDTO(LeaveRequest leave) {
        return new LeaveResponseDTO(
                leave.getId(),
                leave.getEmployee().getId(),
                leave.getEmployee().getEmployeeCode(),
                leave.getEmployee().getName(),
                leave.getEmployee().getDepartment(),
                leave.getLeaveType(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getReason(),
                leave.getStatus(),
                leave.getNumberOfDays(),
                leave.getCreatedAt()
        );
    }
}
