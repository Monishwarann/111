package com.hrm.hrms.repository;

import com.hrm.hrms.entity.LeaveRequest;
import com.hrm.hrms.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveRequest, Long>, JpaSpecificationExecutor<LeaveRequest> {
    List<LeaveRequest> findByEmployeeId(Long employeeId);
    List<LeaveRequest> findByStatus(LeaveStatus status);
    long countByStatus(LeaveStatus status);

    @Query("SELECT l FROM LeaveRequest l WHERE l.employee.id = :employeeId AND l.status <> 'REJECTED' AND " +
           "((l.startDate <= :endDate AND l.endDate >= :startDate))")
    List<LeaveRequest> findOverlappingLeaves(@Param("employeeId") Long employeeId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
}
