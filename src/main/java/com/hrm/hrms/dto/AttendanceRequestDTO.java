package com.hrm.hrms.dto;

import com.hrm.hrms.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceRequestDTO {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private LocalTime checkIn;

    private LocalTime checkOut;

    private AttendanceStatus status;

    public AttendanceRequestDTO() {
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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
}
