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
import static org.mockito.Mockito.when;
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
