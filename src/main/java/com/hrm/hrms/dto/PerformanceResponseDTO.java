package com.hrm.hrms.dto;

import java.time.LocalDate;

public class PerformanceResponseDTO {

    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private LocalDate reviewDate;
    private Double rating;
    private String performanceLevel;
    private String goals;
    private String feedback;
    private String reviewer;

    public PerformanceResponseDTO() {
    }

    public PerformanceResponseDTO(Long id, Long employeeId, String employeeCode, String employeeName, String department, LocalDate reviewDate, Double rating, String performanceLevel, String goals, String feedback, String reviewer) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.department = department;
        this.reviewDate = reviewDate;
        this.rating = rating;
        this.performanceLevel = performanceLevel;
        this.goals = goals;
        this.feedback = feedback;
        this.reviewer = reviewer;
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

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getPerformanceLevel() {
        return performanceLevel;
    }

    public void setPerformanceLevel(String performanceLevel) {
        this.performanceLevel = performanceLevel;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }
}
