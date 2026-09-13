package com.hrm.hrms.dto;

import java.util.List;
import java.util.Map;

public class DashboardSummaryDTO {

    private long totalEmployees;
    private long activeEmployees;
    private long presentToday;
    private long absentToday;
    private long lateToday;
    private long pendingLeaves;
    private long pendingPayroll;
    private double averageRating;
    private double attendancePercentage;

    private Map<String, Long> departmentDistribution;
    private List<EmployeeResponseDTO> recentEmployees;
    private List<LeaveResponseDTO> recentLeaves;
    private List<PayrollResponseDTO> recentPayroll;

    public DashboardSummaryDTO() {
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public long getActiveEmployees() {
        return activeEmployees;
    }

    public void setActiveEmployees(long activeEmployees) {
        this.activeEmployees = activeEmployees;
    }

    public long getPresentToday() {
        return presentToday;
    }

    public void setPresentToday(long presentToday) {
        this.presentToday = presentToday;
    }

    public long getAbsentToday() {
        return absentToday;
    }

    public void setAbsentToday(long absentToday) {
        this.absentToday = absentToday;
    }

    public long getLateToday() {
        return lateToday;
    }

    public void setLateToday(long lateToday) {
        this.lateToday = lateToday;
    }

    public long getPendingLeaves() {
        return pendingLeaves;
    }

    public void setPendingLeaves(long pendingLeaves) {
        this.pendingLeaves = pendingLeaves;
    }

    public long getPendingPayroll() {
        return pendingPayroll;
    }

    public void setPendingPayroll(long pendingPayroll) {
        this.pendingPayroll = pendingPayroll;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public Map<String, Long> getDepartmentDistribution() {
        return departmentDistribution;
    }

    public void setDepartmentDistribution(Map<String, Long> departmentDistribution) {
        this.departmentDistribution = departmentDistribution;
    }

    public List<EmployeeResponseDTO> getRecentEmployees() {
        return recentEmployees;
    }

    public void setRecentEmployees(List<EmployeeResponseDTO> recentEmployees) {
        this.recentEmployees = recentEmployees;
    }

    public List<LeaveResponseDTO> getRecentLeaves() {
        return recentLeaves;
    }

    public void setRecentLeaves(List<LeaveResponseDTO> recentLeaves) {
        this.recentLeaves = recentLeaves;
    }

    public List<PayrollResponseDTO> getRecentPayroll() {
        return recentPayroll;
    }

    public void setRecentPayroll(List<PayrollResponseDTO> recentPayroll) {
        this.recentPayroll = recentPayroll;
    }
}
