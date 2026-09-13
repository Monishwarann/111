package com.hrm.hrms.repository;

import com.hrm.hrms.entity.Attendance;
import com.hrm.hrms.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {
    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);
    List<Attendance> findByEmployeeId(Long employeeId);
    List<Attendance> findByDate(LocalDate date);
    long countByDateAndStatus(LocalDate date, AttendanceStatus status);
    long countByDate(LocalDate date);
}
