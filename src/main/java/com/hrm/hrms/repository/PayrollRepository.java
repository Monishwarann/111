package com.hrm.hrms.repository;

import com.hrm.hrms.entity.Payroll;
import com.hrm.hrms.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long>, JpaSpecificationExecutor<Payroll> {
    List<Payroll> findByEmployeeId(Long employeeId);
    Optional<Payroll> findByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);
    long countByPaymentStatus(PaymentStatus paymentStatus);
}
