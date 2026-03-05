package id.ac.ui.cs.advprog.yomubackend.auth.service;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashed_rahasia");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = authService.register(registerRequest);

        assertNotNull(result);
        assertEquals("ahmad.faiq41", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_UsernameAlreadyExists_ThrowsException() {
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Username sudah terpakai!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class)); // Pastiin fungsi save nggak dipanggil
    }

    @Test
    void login_Success() {
        when(userRepository.findByUsername("ahmad.faiq41")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rahasia", "hashed_rahasia")).thenReturn(true);

        User result = authService.login("ahmad.faiq41", "rahasia");

        assertNotNull(result);
        assertEquals("ahmad.faiq41", result.getUsername());
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        when(userRepository.findByUsername("ahmad.faiq41")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("salah_password", "hashed_rahasia")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login("ahmad.faiq41", "salah_password");
        });

        assertEquals("Username atau password salah!", exception.getMessage());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("hantu")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login("hantu", "rahasia");
        });

        assertEquals("Username atau password salah!", exception.getMessage());
    }
}