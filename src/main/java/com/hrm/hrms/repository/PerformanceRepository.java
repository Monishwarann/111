package com.hrm.hrms.repository;

import com.hrm.hrms.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long>, JpaSpecificationExecutor<Performance> {
    List<Performance> findByEmployeeId(Long employeeId);

    @Query("SELECT AVG(p.rating) FROM Performance p")
    Double findAverageRatingOverall();
}
