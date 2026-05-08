package id.ac.ui.cs.advprog.yomubackend.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.LoginResponse;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.GoogleSsoResult;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;
import java.util.UUID;

import id.ac.ui.cs.advprog.yomubackend.auth.model.Role;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private GoogleIdTokenVerifier googleIdTokenVerifier;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("ahmad.faiq41");
        registerRequest.setEmail("faiq@kampus.id");
        registerRequest.setDisplayName("Faiq");
        registerRequest.setPassword("rahasia");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("ahmad.faiq41");
        user.setEmail("faiq@kampus.id");
        user.setDisplayName("Faiq");
        user.setPassword("hashed_rahasia");
    }

    @Test
    void register_Success() {
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashed_rahasia");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = authService.register(registerRequest);

        assertNotNull(result);
        // Verify the actual User object passed to save() carries the DTO values, not defaults
        ArgumentCaptor<User> savedUserCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(savedUserCaptor.capture());
        User savedUser = savedUserCaptor.getValue();
        assertEquals(registerRequest.getUsername(), savedUser.getUsername());
        assertEquals(registerRequest.getDisplayName(), savedUser.getDisplayName());
        assertEquals(registerRequest.getEmail(), savedUser.getEmail());
        assertEquals("hashed_rahasia", savedUser.getPassword());
    }

    @Test
    void register_UsernameAlreadyExists_ThrowsException() {
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Username sudah terpakai!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Email sudah terdaftar!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        when(userRepository.findByUsername("ahmad.faiq41")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rahasia", "hashed_rahasia")).thenReturn(true);
        when(jwtService.generateToken(anyMap(), eq("ahmad.faiq41"))).thenReturn("dummy_token");

        LoginResponse result = authService.login("ahmad.faiq41", "rahasia");

        assertNotNull(result);
        assertEquals("Login berhasil", result.getMessage());
        assertEquals("dummy_token", result.getToken());
        assertNotNull(result.getUser());
        assertEquals("ahmad.faiq41", result.getUser().getUsername());
        assertEquals("Faiq", result.getUser().getDisplayName());
        assertEquals("faiq@kampus.id", result.getUser().getEmail());
        assertEquals(user.getId(), result.getUser().getUserId());
        assertEquals(Role.PELAJAR, result.getUser().getRole());
    }

    @Test
    @SuppressWarnings("unchecked")
    void login_Success_RoleClaimIsPassedToToken() {
        when(userRepository.findByUsername("ahmad.faiq41")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rahasia", "hashed_rahasia")).thenReturn(true);
        when(jwtService.generateToken(anyMap(), eq("ahmad.faiq41"))).thenReturn("dummy_token");

        authService.login("ahmad.faiq41", "rahasia");

        ArgumentCaptor<java.util.Map<String, Object>> claimsCaptor =
                ArgumentCaptor.forClass(java.util.Map.class);
        verify(jwtService).generateToken(claimsCaptor.capture(), eq("ahmad.faiq41"));
        assertEquals("ROLE_PELAJAR", claimsCaptor.getValue().get("role"));
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        when(userRepository.findByUsername("ahmad.faiq41")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("salah_password", "hashed_rahasia")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login("ahmad.faiq41", "salah_password");
        });

        assertEquals("Username/email atau password salah!", exception.getMessage());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("hantu")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login("hantu", "rahasia");
        });

        assertEquals("Username/email atau password salah!", exception.getMessage());
    }

    @Test
    void login_WithEmail_Success() {
        when(userRepository.findByEmail("faiq@kampus.id")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rahasia", "hashed_rahasia")).thenReturn(true);
        when(jwtService.generateToken(anyMap(), eq("ahmad.faiq41"))).thenReturn("dummy_token");

        LoginResponse result = authService.login("faiq@kampus.id", "rahasia");

        assertNotNull(result);
        assertEquals("Login berhasil", result.getMessage());
        assertEquals("dummy_token", result.getToken());
        assertNotNull(result.getUser());
        assertEquals("faiq@kampus.id", result.getUser().getEmail());
        verify(userRepository, times(1)).findByEmail("faiq@kampus.id");
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void login_WithEmail_WrongPassword_ThrowsException() {
        when(userRepository.findByEmail("faiq@kampus.id")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("salah_password", "hashed_rahasia")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login("faiq@kampus.id", "salah_password");
        });

        assertEquals("Username/email atau password salah!", exception.getMessage());
    }

    @Test
    void login_WithEmail_NotFound_ThrowsException() {
        when(userRepository.findByEmail("nobody@kampus.id")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login("nobody@kampus.id", "rahasia");
        });

        assertEquals("Username/email atau password salah!", exception.getMessage());
    }

    @Test
    void googleLogin_ExistingUser_ReturnsToken() throws Exception {
        GoogleIdToken mockIdToken = mock(GoogleIdToken.class);
        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(googleIdTokenVerifier.verify("valid-token")).thenReturn(mockIdToken);
        when(mockIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("faiq@kampus.id");
        when(payload.get("name")).thenReturn("Faiq");
        when(userRepository.findByEmail("faiq@kampus.id")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyMap(), eq("ahmad.faiq41"))).thenReturn("jwt-token");

        GoogleSsoResult result = authService.googleLogin("valid-token");

        assertFalse(result.isNeedsRegistration());
        assertEquals("Login berhasil", result.getMessage());
        assertEquals("jwt-token", result.getToken());
        assertNotNull(result.getUser());
        assertEquals("ahmad.faiq41", result.getUser().getUsername());
        assertEquals("Faiq", result.getUser().getDisplayName());
        assertEquals("faiq@kampus.id", result.getUser().getEmail());
        assertEquals(user.getId(), result.getUser().getUserId());
        assertNull(result.getEmail());
        assertNull(result.getGoogleName());
        // Existing user must never be modified or re-saved
        verify(userRepository, never()).save(any(User.class));
        // JWT must use the stored username, not any Google-provided value
        verify(jwtService, times(1)).generateToken(anyMap(), eq("ahmad.faiq41"));
    }

    @Test
    void googleLogin_NewUser_ReturnsNeedsRegistration() throws Exception {
        GoogleIdToken mockIdToken = mock(GoogleIdToken.class);
        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(googleIdTokenVerifier.verify("valid-token")).thenReturn(mockIdToken);
        when(mockIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("newuser@kampus.id");
        when(payload.get("name")).thenReturn("New User");
        when(userRepository.findByEmail("newuser@kampus.id")).thenReturn(Optional.empty());

        GoogleSsoResult result = authService.googleLogin("valid-token");

        assertTrue(result.isNeedsRegistration());
        assertEquals("newuser@kampus.id", result.getEmail());
        assertEquals("New User", result.getGoogleName());
        assertNull(result.getToken());
        assertNull(result.getUser());
        // New user must not be saved yet — registration is deferred to the frontend form
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void googleLogin_NewUser_NullGoogleName_ReturnsNeedsRegistration() throws Exception {
        GoogleIdToken mockIdToken = mock(GoogleIdToken.class);
        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(googleIdTokenVerifier.verify("valid-token")).thenReturn(mockIdToken);
        when(mockIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("newuser@kampus.id");
        when(payload.get("name")).thenReturn(null);
        when(userRepository.findByEmail("newuser@kampus.id")).thenReturn(Optional.empty());

        GoogleSsoResult result = authService.googleLogin("valid-token");

        assertTrue(result.isNeedsRegistration());
        assertEquals("newuser@kampus.id", result.getEmail());
        assertNull(result.getGoogleName());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void googleLogin_InvalidToken_ThrowsException() throws Exception {
        when(googleIdTokenVerifier.verify("invalid-token")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authService.googleLogin("invalid-token"));

        assertEquals("Token Google tidak valid!", exception.getMessage());
    }

    @Test
    void googleLogin_VerifyThrowsGeneralSecurityException_ThrowsIllegalArgument() throws Exception {
        when(googleIdTokenVerifier.verify("bad-token")).thenThrow(new GeneralSecurityException("error"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authService.googleLogin("bad-token"));

        assertEquals("Token Google tidak valid!", exception.getMessage());
    }

    @Test
    void googleLogin_VerifyThrowsIOException_ThrowsIllegalArgument() throws Exception {
        when(googleIdTokenVerifier.verify("bad-token")).thenThrow(new IOException("network error"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authService.googleLogin("bad-token"));

        assertEquals("Token Google tidak valid!", exception.getMessage());
    }
}
