package id.ac.ui.cs.advprog.yomubackend.user.service;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.user.dto.UpdateProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("ahmadFaiq41");
        user.setEmail("faiq@kampus.id");
        user.setDisplayName("Faiq");
        user.setPassword("hashed_rahasia");
    }

    @Test
    void updateProfile_Success_UpdateDisplayName() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setDisplayName("Faiq Updated");

        when(userRepository.findByUsername("ahmadFaiq41")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateProfile("ahmadFaiq41", request);

        assertNotNull(result);
        assertEquals("Faiq Updated", result.getDisplayName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateProfile_Success_UpdateUsername() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("newUsername123");

        when(userRepository.findByUsername("ahmadFaiq41")).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("newUsername123")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateProfile("ahmadFaiq41", request);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateProfile_Success_SameUsername_NoDuplicateConflict() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("ahmadFaiq41");

        when(userRepository.findByUsername("ahmadFaiq41")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateProfile("ahmadFaiq41", request);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateProfile_Success_ChangePassword() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setOldPassword("rahasia");
        request.setNewPassword("newSecret123");

        when(userRepository.findByUsername("ahmadFaiq41")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rahasia", "hashed_rahasia")).thenReturn(true);
        when(passwordEncoder.encode("newSecret123")).thenReturn("hashed_newSecret123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateProfile("ahmadFaiq41", request);

        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("newSecret123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateProfile_WrongOldPassword_ThrowsException() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setOldPassword("salahPassword");
        request.setNewPassword("newSecret123");

        when(userRepository.findByUsername("ahmadFaiq41")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("salahPassword", "hashed_rahasia")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile("ahmadFaiq41", request));

        assertEquals("Password lama tidak sesuai!", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateProfile_NewPasswordWithoutOldPassword_ThrowsException() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNewPassword("newSecret123");

        when(userRepository.findByUsername("ahmadFaiq41")).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile("ahmadFaiq41", request));

        assertEquals("Password lama harus diisi untuk mengganti password!", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateProfile_UsernameTakenByOtherUser_ThrowsException() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("takenUsername");

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        anotherUser.setUsername("takenUsername");

        when(userRepository.findByUsername("ahmadFaiq41")).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("takenUsername")).thenReturn(Optional.of(anotherUser));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile("ahmadFaiq41", request));

        assertEquals("Username sudah terpakai!", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateProfile_UserNotFound_ThrowsException() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setDisplayName("New Name");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile("nonexistent", request));

        assertEquals("User tidak ditemukan!", ex.getMessage());
    }

    @Test
    void updateProfile_BlankDisplayName_SkipsDisplayNameUpdate() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setDisplayName("   "); // not null, but blank

        when(userRepository.findByUsername("ahmadFaiq41")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateProfile("ahmadFaiq41", request);

        assertNotNull(result);
        assertEquals("Faiq", result.getDisplayName()); // unchanged
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateProfile_BlankUsername_SkipsUsernameUpdate() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername(""); // not null, but blank

        when(userRepository.findByUsername("ahmadFaiq41")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateProfile("ahmadFaiq41", request);

        assertNotNull(result);
        assertEquals("ahmadFaiq41", result.getUsername()); // unchanged
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateProfile_BlankNewPassword_SkipsPasswordChange() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNewPassword(""); // not null, but blank

        when(userRepository.findByUsername("ahmadFaiq41")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateProfile("ahmadFaiq41", request);

        assertNotNull(result);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteAccount_Success() {
        when(userRepository.findByUsername("ahmadFaiq41")).thenReturn(Optional.of(user));

        userService.deleteAccount("ahmadFaiq41");

        verify(userRepository, times(1)).delete(user);
        verify(eventPublisher, times(1))
                .publishEvent(any(id.ac.ui.cs.advprog.yomubackend.shared.events.auth.UserDeletedEvent.class));
    }

    @Test
    void deleteAccount_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.deleteAccount("nonexistent"));

        assertEquals("User tidak ditemukan!", ex.getMessage());
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void updateProfile_BlankOldPassword_ThrowsException() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setOldPassword("   "); // not null, but blank
        request.setNewPassword("newSecret123");

        when(userRepository.findByUsername("ahmadFaiq41")).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile("ahmadFaiq41", request));

        assertEquals("Password lama harus diisi untuk mengganti password!", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
