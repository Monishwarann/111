package com.hrm.hrms.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "performance_reviews")
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "review_date", nullable = false)
    private LocalDate reviewDate;

    @Column(nullable = false)
    private Double rating;

    @Column(length = 1000)
    private String goals;

    @Column(length = 1000)
    private String feedback;

    @Column(nullable = false)
    private String reviewer;

    public Performance() {
    }

    public Performance(Long id, Employee employee, LocalDate reviewDate, Double rating, String goals, String feedback, String reviewer) {
        this.id = id;
        this.employee = employee;
        this.reviewDate = reviewDate;
        this.rating = rating;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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
