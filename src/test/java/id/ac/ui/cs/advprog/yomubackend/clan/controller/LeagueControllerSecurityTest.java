package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import id.ac.ui.cs.advprog.yomubackend.clan.service.LeagueService;
import id.ac.ui.cs.advprog.yomubackend.config.SecurityConfig;
import id.ac.ui.cs.advprog.yomubackend.security.JwtAuthenticationFilter;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LeagueController.class)
@AutoConfigureMockMvc(addFilters = false)
class LeagueControllerSecurityTest {

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeagueService leagueService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void securityConfig_shouldEnableMethodSecurity() {
        assertTrue(SecurityConfig.class.isAnnotationPresent(EnableMethodSecurity.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void triggerSeasonReset_shouldAllowAdmin() throws Exception {
        mockMvc.perform(post("/api/league/season/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Season reset triggered"));

        verify(leagueService).triggerSeasonReset();
    }

    @Test
    @WithMockUser(roles = "PELAJAR")
    void triggerSeasonReset_shouldRejectNonAdmin() throws Exception {
        ServletException thrown = assertThrows(ServletException.class, () ->
                mockMvc.perform(post("/api/league/season/reset")));

        assertInstanceOf(AuthorizationDeniedException.class, thrown.getCause());
        verify(leagueService, never()).triggerSeasonReset();
    }
}
