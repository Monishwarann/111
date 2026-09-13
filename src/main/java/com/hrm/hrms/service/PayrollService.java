package com.hrm.hrms.service;

import com.hrm.hrms.dto.PayrollRequestDTO;
import com.hrm.hrms.dto.PayrollResponseDTO;
import com.hrm.hrms.entity.Employee;
import com.hrm.hrms.entity.Payroll;
import com.hrm.hrms.enums.PaymentStatus;
import com.hrm.hrms.exception.BadRequestException;
import com.hrm.hrms.exception.ResourceNotFoundException;
import com.hrm.hrms.repository.EmployeeRepository;
import com.hrm.hrms.repository.PayrollRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    public PayrollService(PayrollRepository payrollRepository, EmployeeRepository employeeRepository) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<PayrollResponseDTO> getAllPayroll() {
        return payrollRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PayrollResponseDTO getPayrollById(Long id) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record not found with id: " + id));
        return mapToDTO(payroll);
    }

    public List<PayrollResponseDTO> getPayrollByEmployeeId(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PayrollResponseDTO createPayroll(PayrollRequestDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));

        if (payrollRepository.findByEmployeeIdAndMonthAndYear(dto.getEmployeeId(), dto.getMonth(), dto.getYear()).isPresent()) {
            throw new BadRequestException("Payroll record already exists for employee for " + dto.getMonth() + "/" + dto.getYear());
        }

        validateAmounts(dto.getBasicSalary(), dto.getAllowances(), dto.getDeductions());

        double allowances = dto.getAllowances() != null ? dto.getAllowances() : 0.0;
        double deductions = dto.getDeductions() != null ? dto.getDeductions() : 0.0;
        double netSalary = calculateNetSalary(dto.getBasicSalary(), allowances, deductions);

        Payroll payroll = new Payroll();
        payroll.setEmployee(employee);
        payroll.setMonth(dto.getMonth());
        payroll.setYear(dto.getYear());
        payroll.setBasicSalary(dto.getBasicSalary());
        payroll.setAllowances(allowances);
        payroll.setDeductions(deductions);
        payroll.setNetSalary(netSalary);
        payroll.setPaymentStatus(dto.getPaymentStatus() != null ? dto.getPaymentStatus() : PaymentStatus.PENDING);
        if (payroll.getPaymentStatus() == PaymentStatus.PAID) {
            payroll.setPaymentDate(dto.getPaymentDate() != null ? dto.getPaymentDate() : LocalDate.now());
        } else {
            payroll.setPaymentDate(dto.getPaymentDate());
        }

        Payroll saved = payrollRepository.save(payroll);
        return mapToDTO(saved);
    }

    public PayrollResponseDTO updatePayroll(Long id, PayrollRequestDTO dto) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record not found with id: " + id));

        validateAmounts(dto.getBasicSalary(), dto.getAllowances(), dto.getDeductions());

        double allowances = dto.getAllowances() != null ? dto.getAllowances() : 0.0;
        double deductions = dto.getDeductions() != null ? dto.getDeductions() : 0.0;
        double netSalary = calculateNetSalary(dto.getBasicSalary(), allowances, deductions);

        payroll.setBasicSalary(dto.getBasicSalary());
        payroll.setAllowances(allowances);
        payroll.setDeductions(deductions);
        payroll.setNetSalary(netSalary);
        if (dto.getPaymentStatus() != null) {
            payroll.setPaymentStatus(dto.getPaymentStatus());
            if (dto.getPaymentStatus() == PaymentStatus.PAID && payroll.getPaymentDate() == null) {
                payroll.setPaymentDate(LocalDate.now());
            }
        }

        Payroll updated = payrollRepository.save(payroll);
        return mapToDTO(updated);
    }

    public PayrollResponseDTO markAsPaid(Long id) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record not found with id: " + id));

        payroll.setPaymentStatus(PaymentStatus.PAID);
        payroll.setPaymentDate(LocalDate.now());

        Payroll updated = payrollRepository.save(payroll);
        return mapToDTO(updated);
    }

    public void deletePayroll(Long id) {
        if (!payrollRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payroll record not found with id: " + id);
        }
        payrollRepository.deleteById(id);
    }

    public double calculateNetSalary(Double basicSalary, Double allowances, Double deductions) {
        double basic = basicSalary != null ? basicSalary : 0.0;
        double allow = allowances != null ? allowances : 0.0;
        double ded = deductions != null ? deductions : 0.0;
        return basic + allow - ded;
    }

    private void validateAmounts(Double basicSalary, Double allowances, Double deductions) {
        if (basicSalary != null && basicSalary < 0) {
            throw new BadRequestException("Basic salary cannot be negative");
        }
        if (allowances != null && allowances < 0) {
            throw new BadRequestException("Allowances cannot be negative");
        }
        if (deductions != null && deductions < 0) {
            throw new BadRequestException("Deductions cannot be negative");
        }
    }

    public List<PayrollResponseDTO> generateBatchPayroll(int month, int year) {
        List<Employee> activeEmployees = employeeRepository.findByStatus(com.hrm.hrms.enums.EmployeeStatus.ACTIVE);
        for (Employee emp : activeEmployees) {
            if (payrollRepository.findByEmployeeIdAndMonthAndYear(emp.getId(), month, year).isEmpty()) {
                double basic = emp.getSalary() != null && emp.getSalary() > 0 ? emp.getSalary() : 5000.0;
                double allowances = Math.round(basic * 0.15 * 100.0) / 100.0;
                double deductions = Math.round(basic * 0.05 * 100.0) / 100.0;
                double net = basic + allowances - deductions;

                Payroll p = new Payroll();
                p.setEmployee(emp);
                p.setMonth(month);
                p.setYear(year);
                p.setBasicSalary(basic);
                p.setAllowances(allowances);
                p.setDeductions(deductions);
                p.setNetSalary(net);
                p.setPaymentStatus(PaymentStatus.PENDING);
                payrollRepository.save(p);
            }
        }
        return getAllPayroll();
    }

    public PayrollResponseDTO mapToDTO(Payroll payroll) {
        return new PayrollResponseDTO(
                payroll.getId(),
                payroll.getEmployee().getId(),
                payroll.getEmployee().getEmployeeCode(),
                payroll.getEmployee().getName(),
                payroll.getEmployee().getDepartment(),
                payroll.getEmployee().getDesignation(),
                payroll.getMonth(),
                payroll.getYear(),
                payroll.getBasicSalary(),
                payroll.getAllowances(),
                payroll.getDeductions(),
                payroll.getNetSalary(),
                payroll.getPaymentStatus(),
                payroll.getPaymentDate()
        );
    }
}
