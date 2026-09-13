package com.hrm.hrms.repository;

import com.hrm.hrms.entity.Employee;
import com.hrm.hrms.enums.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByEmployeeCode(String employeeCode);
    Optional<Employee> findByEmail(String email);
    boolean existsByEmployeeCode(String employeeCode);
    boolean existsByEmail(String email);
    List<Employee> findByStatus(EmployeeStatus status);
    long countByStatus(EmployeeStatus status);
}
