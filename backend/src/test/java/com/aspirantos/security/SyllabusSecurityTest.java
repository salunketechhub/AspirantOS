package com.aspirantos.security;

import com.aspirantos.config.CorsConfig;
import com.aspirantos.config.SecurityConfig;
import com.aspirantos.controller.SyllabusController;
import com.aspirantos.dto.syllabus.ExamResponse;
import com.aspirantos.entity.ExamStage;
import com.aspirantos.entity.Role;
import com.aspirantos.entity.User;
import com.aspirantos.service.SyllabusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SyllabusController.class)
@Import({
        SecurityConfig.class,
        CorsConfig.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class
})
class SyllabusSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SyllabusService syllabusService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Aarav")
                .lastName("Sharma")
                .email("aarav.sharma@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("GET /api/syllabus/exams without token should return 401 Unauthorized")
    void getExamsWithoutTokenShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/syllabus/exams"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("GET /api/syllabus/exams with valid Bearer token should return 200 OK")
    void getExamsWithValidTokenShouldReturn200() throws Exception {
        when(jwtService.extractUsername("valid.jwt.token")).thenReturn("aarav.sharma@example.com");
        when(userDetailsService.loadUserByUsername("aarav.sharma@example.com")).thenReturn(testUser);
        when(jwtService.isTokenValid("valid.jwt.token", testUser)).thenReturn(true);

        when(syllabusService.getAllExams()).thenReturn(List.of(
                ExamResponse.builder()
                        .id(UUID.randomUUID())
                        .code("PRELIMS")
                        .name("UPSC Prelims")
                        .stage(ExamStage.PRELIMS)
                        .build()
        ));

        mockMvc.perform(get("/api/syllabus/exams")
                        .header("Authorization", "Bearer valid.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("PRELIMS"));
    }
}
