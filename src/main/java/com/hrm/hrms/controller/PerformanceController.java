package com.hrm.hrms.controller;

import com.hrm.hrms.dto.PerformanceRequestDTO;
import com.hrm.hrms.dto.PerformanceResponseDTO;
import com.hrm.hrms.service.PerformanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/performance", "/performance"})
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping
    public ResponseEntity<List<PerformanceResponseDTO>> getAllPerformance() {
        return ResponseEntity.ok(performanceService.getAllPerformance());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceResponseDTO> getPerformanceById(@PathVariable Long id) {
        return ResponseEntity.ok(performanceService.getPerformanceById(id));
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<List<PerformanceResponseDTO>> getPerformanceByEmployeeId(@PathVariable("id") Long employeeId) {
        return ResponseEntity.ok(performanceService.getPerformanceByEmployeeId(employeeId));
    }

    @PostMapping
    public ResponseEntity<PerformanceResponseDTO> createPerformance(@Valid @RequestBody PerformanceRequestDTO dto) {
        PerformanceResponseDTO created = performanceService.createPerformance(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerformanceResponseDTO> updatePerformance(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceRequestDTO dto) {
        PerformanceResponseDTO updated = performanceService.updatePerformance(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformance(@PathVariable Long id) {
        performanceService.deletePerformance(id);
        return ResponseEntity.noContent().build();
    }
}
