package com.hrm.hrms.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class PerformanceRequestDTO {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Review date is required")
    private LocalDate reviewDate;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Double rating;

    private String goals;

    private String feedback;

    @NotBlank(message = "Reviewer is required")
    private String reviewer;

    public PerformanceRequestDTO() {
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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
