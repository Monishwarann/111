package com.hrm.hrms.controller;

import com.hrm.hrms.dto.LeaveRequestDTO;
import com.hrm.hrms.dto.LeaveResponseDTO;
import com.hrm.hrms.enums.LeaveStatus;
import com.hrm.hrms.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/leaves", "/leaves"})
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping
    public ResponseEntity<List<LeaveResponseDTO>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveResponseDTO> getLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.getLeaveById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LeaveResponseDTO>> getLeavesByStatus(@PathVariable LeaveStatus status) {
        return ResponseEntity.ok(leaveService.getLeavesByStatus(status));
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<List<LeaveResponseDTO>> getLeavesByEmployeeId(@PathVariable("id") Long employeeId) {
        return ResponseEntity.ok(leaveService.getLeavesByEmployeeId(employeeId));
    }

    @PostMapping
    public ResponseEntity<LeaveResponseDTO> applyLeave(@Valid @RequestBody LeaveRequestDTO dto) {
        LeaveResponseDTO created = leaveService.applyLeave(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveResponseDTO> approveLeave(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.approveLeave(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<LeaveResponseDTO> rejectLeave(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.rejectLeave(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeave(@PathVariable Long id) {
        leaveService.deleteLeave(id);
        return ResponseEntity.noContent().build();
    }
}
