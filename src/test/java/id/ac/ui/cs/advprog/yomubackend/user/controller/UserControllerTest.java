package id.ac.ui.cs.advprog.yomubackend.user.controller;

import tools.jackson.databind.json.JsonMapper;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.security.JwtService;
import id.ac.ui.cs.advprog.yomubackend.user.dto.UpdateProfileRequest;
import id.ac.ui.cs.advprog.yomubackend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JsonMapper jsonMapper;

    private User updatedUser;

    @BeforeEach
    void setUp() {
        updatedUser = new User();
        updatedUser.setId(UUID.randomUUID());
        updatedUser.setUsername("ahmadFaiq41");
        updatedUser.setEmail("faiq@kampus.id");
        updatedUser.setDisplayName("Faiq Updated");
        updatedUser.setPassword("hashed_rahasia");
    }

    @Test
    @WithMockUser(username = "ahmadFaiq41")
    void updateProfile_Success() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setDisplayName("Faiq Updated");

        when(userService.updateProfile(anyString(), any(UpdateProfileRequest.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profil berhasil diperbarui"))
                .andExpect(jsonPath("$.username").value("ahmadFaiq41"))
                .andExpect(jsonPath("$.displayName").value("Faiq Updated"));
    }

    @Test
    @WithMockUser(username = "ahmadFaiq41")
    void updateProfile_UsernameConflict_ReturnsBadRequest() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("takenUsername");

        when(userService.updateProfile(anyString(), any(UpdateProfileRequest.class)))
                .thenThrow(new IllegalArgumentException("Username sudah terpakai!"));

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username sudah terpakai!"));
    }

    @Test
    @WithMockUser(username = "ahmadFaiq41")
    void updateProfile_WrongOldPassword_ReturnsBadRequest() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setOldPassword("salah");
        request.setNewPassword("newPass123");

        when(userService.updateProfile(anyString(), any(UpdateProfileRequest.class)))
                .thenThrow(new IllegalArgumentException("Password lama tidak sesuai!"));

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Password lama tidak sesuai!"));
    }

    @Test
    @WithMockUser(username = "ahmadFaiq41")
    void updateProfile_InvalidUsername_ReturnsBadRequest() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("invalid.user!");

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username hanya boleh mengandung huruf dan angka!"));
    }

    @Test
    @WithMockUser(username = "ahmadFaiq41")
    void deleteAccount_Success() throws Exception {
        doNothing().when(userService).deleteAccount("ahmadFaiq41");

        mockMvc.perform(delete("/api/users/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Akun berhasil dihapus"));
    }

    @Test
    @WithMockUser(username = "ghostUser")
    void deleteAccount_UserNotFound_ReturnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("User tidak ditemukan!"))
                .when(userService).deleteAccount("ghostUser");

        mockMvc.perform(delete("/api/users/account"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User tidak ditemukan!"));
    }

    @Test
    @WithMockUser(username = "ahmadFaiq41")
    void getUserProfile_Success() throws Exception {
        UUID userId = updatedUser.getId();
        when(userService.getUserById(userId)).thenReturn(updatedUser);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ahmadFaiq41"))
                .andExpect(jsonPath("$.displayName").value("Faiq Updated"));
    }

    @Test
    @WithMockUser(username = "ahmadFaiq41")
    void getUserProfile_NotFound_Returns404() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userService.getUserById(userId))
                .thenThrow(new IllegalArgumentException("User tidak ditemukan!"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "ahmadFaiq41")
    void updateProfile_EmptyUsernamePassesValidation() throws Exception {
        // Sending "" for username means "don't change it" — must not trigger @Pattern
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setDisplayName("Faiq Updated");
        request.setUsername("");
        request.setOldPassword("");
        request.setNewPassword("");

        when(userService.updateProfile(anyString(), any(UpdateProfileRequest.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profil berhasil diperbarui"));
    }
}
