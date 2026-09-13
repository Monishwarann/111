package com.hrm.hrms.dto;

import com.hrm.hrms.enums.PaymentStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class PayrollRequestDTO {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be valid")
    private Integer year;

    @NotNull(message = "Basic salary is required")
    @PositiveOrZero(message = "Basic salary cannot be negative")
    private Double basicSalary;

    @PositiveOrZero(message = "Allowances cannot be negative")
    private Double allowances = 0.0;

    @PositiveOrZero(message = "Deductions cannot be negative")
    private Double deductions = 0.0;

    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private LocalDate paymentDate;

    public PayrollRequestDTO() {
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(Double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public Double getAllowances() {
        return allowances;
    }

    public void setAllowances(Double allowances) {
        this.allowances = allowances;
    }

    public Double getDeductions() {
        return deductions;
    }

    public void setDeductions(Double deductions) {
        this.deductions = deductions;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
