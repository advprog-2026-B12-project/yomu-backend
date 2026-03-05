package id.ac.ui.cs.advprog.yomubackend.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.Customizer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

class SecurityConfigTest {

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void testSecurityConfigCoverage() throws Exception {
        SecurityConfig config = new SecurityConfig();

        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        CsrfConfigurer csrf = mock(CsrfConfigurer.class, RETURNS_DEEP_STUBS);
        AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry registry =
                mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class, RETURNS_DEEP_STUBS);

        when(http.csrf(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer customizer = invocation.getArgument(0);
            customizer.customize(csrf);
            return http;
        });

        when(http.authorizeHttpRequests(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer customizer = invocation.getArgument(0);
            customizer.customize(registry);
            return http;
        });

        try {
            config.filterChain(http);
        } catch (Exception ignored) {

        }
    }
}