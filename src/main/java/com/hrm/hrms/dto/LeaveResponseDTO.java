package com.hrm.hrms.dto;

import com.hrm.hrms.enums.LeaveStatus;
import com.hrm.hrms.enums.LeaveType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveResponseDTO {

    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
    private Integer numberOfDays;
    private LocalDateTime createdAt;

    public LeaveResponseDTO() {
    }

    public LeaveResponseDTO(Long id, Long employeeId, String employeeCode, String employeeName, String department, LeaveType leaveType, LocalDate startDate, LocalDate endDate, String reason, LeaveStatus status, Integer numberOfDays, LocalDateTime createdAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.department = department;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.numberOfDays = numberOfDays;
        this.createdAt = createdAt;
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

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(Integer numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
