package com.hrm.hrms.entity;

import com.hrm.hrms.enums.PaymentStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payrolls", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "payroll_month", "payroll_year"})
})
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "payroll_month", nullable = false)
    private Integer month;

    @Column(name = "payroll_year", nullable = false)
    private Integer year;

    @Column(name = "basic_salary", nullable = false)
    private Double basicSalary;

    private Double allowances = 0.0;

    private Double deductions = 0.0;

    @Column(name = "net_salary", nullable = false)
    private Double netSalary;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    public Payroll() {
    }

    public Payroll(Long id, Employee employee, Integer month, Integer year, Double basicSalary, Double allowances, Double deductions, Double netSalary, PaymentStatus paymentStatus, LocalDate paymentDate) {
        this.id = id;
        this.employee = employee;
        this.month = month;
        this.year = year;
        this.basicSalary = basicSalary;
        this.allowances = allowances != null ? allowances : 0.0;
        this.deductions = deductions != null ? deductions : 0.0;
        this.netSalary = netSalary;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PENDING;
        this.paymentDate = paymentDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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

    public Double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(Double netSalary) {
        this.netSalary = netSalary;
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
