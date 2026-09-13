package com.hrm.hrms.service;

import com.hrm.hrms.dto.LoginRequestDTO;
import com.hrm.hrms.dto.LoginResponseDTO;
import com.hrm.hrms.entity.User;
import com.hrm.hrms.exception.BadRequestException;
import com.hrm.hrms.exception.ResourceNotFoundException;
import com.hrm.hrms.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Long employeeId = user.getEmployee() != null ? user.getEmployee().getId() : null;
            String employeeName = user.getEmployee() != null ? user.getEmployee().getName() : user.getUsername();

            return new LoginResponseDTO(
                    user.getUsername(),
                    user.getRole(),
                    employeeId,
                    employeeName,
                    "Login successful"
            );
        } catch (Exception e) {
            throw new BadRequestException("Invalid username or password");
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BadRequestException("User is not authenticated");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Current logged-in user not found"));
    }

    public LoginResponseDTO getCurrentUserDTO() {
        User user = getCurrentUser();
        Long employeeId = user.getEmployee() != null ? user.getEmployee().getId() : null;
        String employeeName = user.getEmployee() != null ? user.getEmployee().getName() : user.getUsername();

        return new LoginResponseDTO(
                user.getUsername(),
                user.getRole(),
                employeeId,
                employeeName,
                "Current user details"
        );
    }
}
