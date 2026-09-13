package com.hrm.hrms.service;

import com.hrm.hrms.dto.AttendanceRequestDTO;
import com.hrm.hrms.dto.AttendanceResponseDTO;
import com.hrm.hrms.entity.Attendance;
import com.hrm.hrms.entity.Employee;
import com.hrm.hrms.enums.AttendanceStatus;
import com.hrm.hrms.exception.BadRequestException;
import com.hrm.hrms.exception.ResourceNotFoundException;
import com.hrm.hrms.repository.AttendanceRepository;
import com.hrm.hrms.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<AttendanceResponseDTO> getAllAttendance() {
        return attendanceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AttendanceResponseDTO getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));
        return mapToDTO(attendance);
    }

    public List<AttendanceResponseDTO> getAttendanceByEmployeeId(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AttendanceResponseDTO> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AttendanceResponseDTO markAttendance(AttendanceRequestDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));

        if (attendanceRepository.findByEmployeeIdAndDate(dto.getEmployeeId(), dto.getDate()).isPresent()) {
            throw new BadRequestException("Attendance record already exists for employee on " + dto.getDate());
        }

        validateTimes(dto.getCheckIn(), dto.getCheckOut());

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(dto.getDate());
        attendance.setCheckIn(dto.getCheckIn());
        attendance.setCheckOut(dto.getCheckOut());

        Double workingHours = calculateWorkingHours(dto.getCheckIn(), dto.getCheckOut());
        attendance.setWorkingHours(workingHours);

        AttendanceStatus status = dto.getStatus();
        if (status == null) {
            status = determineStatus(dto.getCheckIn(), dto.getCheckOut(), workingHours);
        }
        attendance.setStatus(status);

        Attendance saved = attendanceRepository.save(attendance);
        return mapToDTO(saved);
    }

    public AttendanceResponseDTO updateAttendance(Long id, AttendanceRequestDTO dto) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));

        validateTimes(dto.getCheckIn(), dto.getCheckOut());

        attendance.setCheckIn(dto.getCheckIn());
        attendance.setCheckOut(dto.getCheckOut());

        Double workingHours = calculateWorkingHours(dto.getCheckIn(), dto.getCheckOut());
        attendance.setWorkingHours(workingHours);

        if (dto.getStatus() != null) {
            attendance.setStatus(dto.getStatus());
        } else {
            attendance.setStatus(determineStatus(dto.getCheckIn(), dto.getCheckOut(), workingHours));
        }

        Attendance updated = attendanceRepository.save(attendance);
        return mapToDTO(updated);
    }

    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance record not found with id: " + id);
        }
        attendanceRepository.deleteById(id);
    }

    private void validateTimes(LocalTime checkIn, LocalTime checkOut) {
        if (checkIn != null && checkOut != null && checkOut.isBefore(checkIn)) {
            throw new BadRequestException("Check-out time cannot be before check-in time");
        }
    }

    public Double calculateWorkingHours(LocalTime checkIn, LocalTime checkOut) {
        if (checkIn == null || checkOut == null) {
            return 0.0;
        }
        long minutes = Duration.between(checkIn, checkOut).toMinutes();
        double hours = minutes / 60.0;
        return Math.round(hours * 100.0) / 100.0;
    }

    private AttendanceStatus determineStatus(LocalTime checkIn, LocalTime checkOut, Double workingHours) {
        if (checkIn == null) {
            return AttendanceStatus.ABSENT;
        }
        if (workingHours != null && workingHours > 0 && workingHours < 5.0) {
            return AttendanceStatus.HALF_DAY;
        }
        if (checkIn.isAfter(LocalTime.of(9, 15))) {
            return AttendanceStatus.LATE;
        }
        return AttendanceStatus.PRESENT;
    }

    public AttendanceResponseDTO mapToDTO(Attendance attendance) {
        return new AttendanceResponseDTO(
                attendance.getId(),
                attendance.getEmployee().getId(),
                attendance.getEmployee().getEmployeeCode(),
                attendance.getEmployee().getName(),
                attendance.getEmployee().getDepartment(),
                attendance.getDate(),
                attendance.getCheckIn(),
                attendance.getCheckOut(),
                attendance.getStatus(),
                attendance.getWorkingHours()
        );
    }
}
