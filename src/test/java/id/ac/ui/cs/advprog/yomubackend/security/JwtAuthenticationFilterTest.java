package id.ac.ui.cs.advprog.yomubackend.security;

import id.ac.ui.cs.advprog.yomubackend.auth.model.Role;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private User testUser;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        
        SecurityContextHolder.clearContext();
    }
    
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_NoAuthHeader_ContinuesFilterChain() throws ServletException, IOException {
        jwtAuthenticationFilter.doFilter(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtService, userRepository);
    }

    @Test
    void doFilterInternal_InvalidAuthHeader_ContinuesFilterChain() throws ServletException, IOException {
        request.addHeader("Authorization", "InvalidHeader ");
        jwtAuthenticationFilter.doFilter(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtService, userRepository);
    }

    @Test
    void doFilterInternal_ValidToken_AuthenticatesUser() throws ServletException, IOException {
        String token = "valid-token";
        request.addHeader("Authorization", "Bearer " + token);
        testUser.setRole(Role.PELAJAR);

        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(token, "testuser")).thenReturn(true);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
    }

    @Test
    void doFilterInternal_PelajarToken_AuthoritiesContainRolePelajar() throws ServletException, IOException {
        String token = "pelajar-token";
        request.addHeader("Authorization", "Bearer " + token);
        testUser.setRole(Role.PELAJAR);

        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(token, "testuser")).thenReturn(true);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PELAJAR")));
    }

    @Test
    void doFilterInternal_AdminToken_AuthoritiesContainRoleAdmin() throws ServletException, IOException {
        String token = "admin-token";
        request.addHeader("Authorization", "Bearer " + token);
        testUser.setRole(Role.ADMIN);

        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(token, "testuser")).thenReturn(true);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void doFilterInternal_ValidTokenUserNotFound_ContinuesWithoutAuthentication() throws ServletException, IOException {
        String token = "valid-token";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtService.extractUsername(token)).thenReturn("notfounduser");
        when(userRepository.findByUsername("notfounduser")).thenReturn(Optional.empty());
        
        jwtAuthenticationFilter.doFilter(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ValidTokenButInvalidVerification_ContinuesWithoutAuthentication() throws ServletException, IOException {
        String token = "valid-token";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(token, "testuser")).thenReturn(false);
        
        jwtAuthenticationFilter.doFilter(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ExceptionThrown_ContinuesWithoutAuthentication() throws ServletException, IOException {
        String token = "invalid-token";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("Malformed token"));
        
        jwtAuthenticationFilter.doFilter(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_AlreadyAuthenticated_ContinuesWithoutReAuthentication() throws ServletException, IOException {
        String token = "valid-token";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        
        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(anotherUser, null, new java.util.ArrayList<>())
        );
        
        jwtAuthenticationFilter.doFilter(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userRepository);
        assertEquals("anotheruser", ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
    }
}
