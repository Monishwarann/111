package com.hrm.hrms.controller;

import com.hrm.hrms.dto.AttendanceRequestDTO;
import com.hrm.hrms.dto.AttendanceResponseDTO;
import com.hrm.hrms.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping({"/api/attendance", "/attendance"})
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponseDTO>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponseDTO> getAttendanceById(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.getAttendanceById(id));
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<List<AttendanceResponseDTO>> getAttendanceByEmployeeId(@PathVariable("id") Long employeeId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByEmployeeId(employeeId));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<AttendanceResponseDTO>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAttendanceByDate(date));
    }

    @PostMapping
    public ResponseEntity<AttendanceResponseDTO> markAttendance(@Valid @RequestBody AttendanceRequestDTO dto) {
        AttendanceResponseDTO created = attendanceService.markAttendance(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceResponseDTO> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceRequestDTO dto) {
        AttendanceResponseDTO updated = attendanceService.updateAttendance(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }
}
