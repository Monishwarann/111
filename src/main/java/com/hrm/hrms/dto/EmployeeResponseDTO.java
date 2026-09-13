package com.hrm.hrms.dto;

import com.hrm.hrms.enums.EmployeeStatus;
import java.time.LocalDate;

public class EmployeeResponseDTO {

    private Long id;
    private String employeeCode;
    private String name;
    private String email;
    private String phone;
    private String department;
    private String designation;
    private LocalDate joiningDate;
    private Double salary;
    private EmployeeStatus status;

    public EmployeeResponseDTO() {
    }

    public EmployeeResponseDTO(Long id, String employeeCode, String name, String email, String phone, String department, String designation, LocalDate joiningDate, Double salary, EmployeeStatus status) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.designation = designation;
        this.joiningDate = joiningDate;
        this.salary = salary;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }
}
