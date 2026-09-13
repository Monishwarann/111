package com.hrm.hrms.controller;

import com.hrm.hrms.dto.LoginRequestDTO;
import com.hrm.hrms.dto.LoginResponseDTO;
import com.hrm.hrms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/auth", "/auth"})
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletRequest request) {
        LoginResponseDTO response = userService.login(loginRequest, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponseDTO> getCurrentUser() {
        LoginResponseDTO response = userService.getCurrentUserDTO();
        return ResponseEntity.ok(response);
    }
}
