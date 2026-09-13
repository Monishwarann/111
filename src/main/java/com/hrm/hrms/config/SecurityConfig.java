package com.hrm.hrms.config;

import com.hrm.hrms.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        user.isEnabled(),
                        true, true, true,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/style.css", "/script.js", "/favicon.ico", "/api/auth/login", "/api/auth/me", "/api/auth/logout", "/auth/login", "/auth/me", "/auth/logout").permitAll()
                .requestMatchers("/api/dashboard/**", "/dashboard/**").hasAnyRole("ADMIN", "HR", "EMPLOYEE")
                .requestMatchers(HttpMethod.GET, "/api/employees/**", "/employees/**").hasAnyRole("ADMIN", "HR", "EMPLOYEE")
                .requestMatchers("/api/employees/**", "/employees/**").hasAnyRole("ADMIN", "HR")
                .requestMatchers(HttpMethod.GET, "/api/attendance/**", "/attendance/**").hasAnyRole("ADMIN", "HR", "EMPLOYEE")
                .requestMatchers("/api/attendance/**", "/attendance/**").hasAnyRole("ADMIN", "HR", "EMPLOYEE")
                .requestMatchers(HttpMethod.GET, "/api/leaves/**", "/leaves/**").hasAnyRole("ADMIN", "HR", "EMPLOYEE")
                .requestMatchers(HttpMethod.POST, "/api/leaves/**", "/leaves/**").hasAnyRole("ADMIN", "HR", "EMPLOYEE")
                .requestMatchers("/api/leaves/*/approve", "/api/leaves/*/reject", "/leaves/*/approve", "/leaves/*/reject").hasAnyRole("ADMIN", "HR")
                .requestMatchers("/api/leaves/**", "/leaves/**").hasAnyRole("ADMIN", "HR")
                .requestMatchers(HttpMethod.GET, "/api/payroll/**", "/payroll/**").hasAnyRole("ADMIN", "HR", "EMPLOYEE")
                .requestMatchers("/api/payroll/**", "/payroll/**").hasAnyRole("ADMIN", "HR")
                .requestMatchers(HttpMethod.GET, "/api/performance/**", "/performance/**").hasAnyRole("ADMIN", "HR", "EMPLOYEE")
                .requestMatchers("/api/performance/**", "/performance/**").hasAnyRole("ADMIN", "HR")
                .requestMatchers("/api/reports/**", "/reports/**").hasAnyRole("ADMIN", "HR")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"status\":401,\"message\":\"Unauthorized access: Please login first\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"status\":403,\"message\":\"Access denied: Insufficient privileges\"}");
                })
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Logged out successfully\"}");
                })
            );

        return http.build();
    }
}
