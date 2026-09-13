package com.hrm.hrms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testLoginEndpoint() throws Exception {
        String body = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void testUnauthorizedAccessBlocked() throws Exception {
        mockMvc.perform(get("/api/reports/employees"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminAccessAllowed() throws Exception {
        mockMvc.perform(get("/api/reports/employees"))
                .andExpect(status().isOk());
    }
}
