package com.hrm.hrms.entity;

import com.hrm.hrms.enums.AttendanceStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendance", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "date"})
})
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "check_in")
    private LocalTime checkIn;

    @Column(name = "check_out")
    private LocalTime checkOut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    @Column(name = "working_hours")
    private Double workingHours;

    public Attendance() {
    }

    public Attendance(Long id, Employee employee, LocalDate date, LocalTime checkIn, LocalTime checkOut, AttendanceStatus status, Double workingHours) {
        this.id = id;
        this.employee = employee;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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
