package com.hrm.hrms.dto;

import com.hrm.hrms.enums.Role;

public class LoginResponseDTO {

    private String username;
    private Role role;
    private Long employeeId;
    private String employeeName;
    private String message;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String username, Role role, Long employeeId, String employeeName, String message) {
        this.username = username;
        this.role = role;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
