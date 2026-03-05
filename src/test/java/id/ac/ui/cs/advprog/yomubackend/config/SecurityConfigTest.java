package id.ac.ui.cs.advprog.yomubackend.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Test
    void testPasswordEncoder() {
        SecurityConfig config = new SecurityConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void testFilterChainCoverage() throws Exception {
        SecurityConfig config = new SecurityConfig();

        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);

        when(http.csrf(any())).thenAnswer(invocation -> {
            Customizer customizer = invocation.getArgument(0);
            customizer.customize(mock(CsrfConfigurer.class));
            return http;
        });

        when(http.sessionManagement(any())).thenAnswer(invocation -> {
            Customizer customizer = invocation.getArgument(0);
            customizer.customize(mock(SessionManagementConfigurer.class, RETURNS_DEEP_STUBS));
            return http;
        });

        when(http.authorizeHttpRequests(any())).thenAnswer(invocation -> {
            Customizer customizer = invocation.getArgument(0);
            customizer.customize(mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class, RETURNS_DEEP_STUBS));
            return http;
        });

        DefaultSecurityFilterChain mockChain = mock(DefaultSecurityFilterChain.class);
        when(http.build()).thenReturn(mockChain);

        SecurityFilterChain result = config.filterChain(http);

        assertNotNull(result);
    }
}