package com.hrm.hrms.service;

import com.hrm.hrms.dto.PerformanceRequestDTO;
import com.hrm.hrms.dto.PerformanceResponseDTO;
import com.hrm.hrms.entity.Employee;
import com.hrm.hrms.entity.Performance;
import com.hrm.hrms.exception.BadRequestException;
import com.hrm.hrms.exception.ResourceNotFoundException;
import com.hrm.hrms.repository.EmployeeRepository;
import com.hrm.hrms.repository.PerformanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final EmployeeRepository employeeRepository;

    public PerformanceService(PerformanceRepository performanceRepository, EmployeeRepository employeeRepository) {
        this.performanceRepository = performanceRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<PerformanceResponseDTO> getAllPerformance() {
        return performanceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PerformanceResponseDTO getPerformanceById(Long id) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Performance review not found with id: " + id));
        return mapToDTO(performance);
    }

    public List<PerformanceResponseDTO> getPerformanceByEmployeeId(Long employeeId) {
        return performanceRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PerformanceResponseDTO createPerformance(PerformanceRequestDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));

        if (dto.getRating() < 1.0 || dto.getRating() > 5.0) {
            throw new BadRequestException("Rating must be between 1.0 and 5.0");
        }

        Performance performance = new Performance();
        performance.setEmployee(employee);
        performance.setReviewDate(dto.getReviewDate());
        performance.setRating(dto.getRating());
        performance.setGoals(dto.getGoals());
        performance.setFeedback(dto.getFeedback());
        performance.setReviewer(dto.getReviewer());

        Performance saved = performanceRepository.save(performance);
        return mapToDTO(saved);
    }

    public PerformanceResponseDTO updatePerformance(Long id, PerformanceRequestDTO dto) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Performance review not found with id: " + id));

        if (dto.getRating() < 1.0 || dto.getRating() > 5.0) {
            throw new BadRequestException("Rating must be between 1.0 and 5.0");
        }

        performance.setReviewDate(dto.getReviewDate());
        performance.setRating(dto.getRating());
        performance.setGoals(dto.getGoals());
        performance.setFeedback(dto.getFeedback());
        performance.setReviewer(dto.getReviewer());

        Performance updated = performanceRepository.save(performance);
        return mapToDTO(updated);
    }

    public void deletePerformance(Long id) {
        if (!performanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Performance review not found with id: " + id);
        }
        performanceRepository.deleteById(id);
    }

    public String classifyRating(Double rating) {
        if (rating == null) return "N/A";
        if (rating >= 4.5) return "Excellent";
        if (rating >= 3.5) return "Good";
        if (rating >= 2.5) return "Average";
        return "Needs Improvement";
    }

    public PerformanceResponseDTO mapToDTO(Performance performance) {
        return new PerformanceResponseDTO(
                performance.getId(),
                performance.getEmployee().getId(),
                performance.getEmployee().getEmployeeCode(),
                performance.getEmployee().getName(),
                performance.getEmployee().getDepartment(),
                performance.getReviewDate(),
                performance.getRating(),
                classifyRating(performance.getRating()),
                performance.getGoals(),
                performance.getFeedback(),
                performance.getReviewer()
        );
    }
}
