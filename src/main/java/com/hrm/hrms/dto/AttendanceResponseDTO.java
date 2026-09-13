package com.hrm.hrms.dto;

import com.hrm.hrms.enums.AttendanceStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceResponseDTO {

    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private LocalDate date;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private AttendanceStatus status;
    private Double workingHours;

    public AttendanceResponseDTO() {
    }

    public AttendanceResponseDTO(Long id, Long employeeId, String employeeCode, String employeeName, String department, LocalDate date, LocalTime checkIn, LocalTime checkOut, AttendanceStatus status, Double workingHours) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.department = department;
        this.date = date;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.workingHours = workingHours;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalTime checkOut) {
        this.checkOut = checkOut;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public Double getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(Double workingHours) {
        this.workingHours = workingHours;
    }
}
