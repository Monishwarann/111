package com.hrm.hrms.controller;

import com.hrm.hrms.dto.PayrollRequestDTO;
import com.hrm.hrms.dto.PayrollResponseDTO;
import com.hrm.hrms.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/payroll", "/payroll"})
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping
    public ResponseEntity<List<PayrollResponseDTO>> getAllPayroll() {
        return ResponseEntity.ok(payrollService.getAllPayroll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayrollResponseDTO> getPayrollById(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.getPayrollById(id));
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<List<PayrollResponseDTO>> getPayrollByEmployeeId(@PathVariable("id") Long employeeId) {
        return ResponseEntity.ok(payrollService.getPayrollByEmployeeId(employeeId));
    }

    @PostMapping
    public ResponseEntity<PayrollResponseDTO> createPayroll(@Valid @RequestBody PayrollRequestDTO dto) {
        PayrollResponseDTO created = payrollService.createPayroll(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PayrollResponseDTO> updatePayroll(
            @PathVariable Long id,
            @Valid @RequestBody PayrollRequestDTO dto) {
        PayrollResponseDTO updated = payrollService.updatePayroll(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<PayrollResponseDTO> markPayrollPaid(@PathVariable Long id) {
        PayrollResponseDTO updated = payrollService.markAsPaid(id);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<PayrollResponseDTO>> generateBatchPayroll(
            @RequestParam(defaultValue = "9") int month,
            @RequestParam(defaultValue = "2026") int year) {
        return ResponseEntity.ok(payrollService.generateBatchPayroll(month, year));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayroll(@PathVariable Long id) {
        payrollService.deletePayroll(id);
        return ResponseEntity.noContent().build();
    }
}
