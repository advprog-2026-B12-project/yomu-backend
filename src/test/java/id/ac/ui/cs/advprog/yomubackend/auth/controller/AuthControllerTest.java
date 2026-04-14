package id.ac.ui.cs.advprog.yomubackend.auth.controller;

import tools.jackson.databind.json.JsonMapper;

import id.ac.ui.cs.advprog.yomubackend.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.LoginResponse;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.auth.service.AuthService;
import id.ac.ui.cs.advprog.yomubackend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AuthService authService;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private UserRepository userRepository;

        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        @Autowired
        private JsonMapper jsonMapper;

        private RegisterRequest registerRequest;
        private LoginRequest loginRequest;
        private User user;

        @BeforeEach
        void setUp() {
                registerRequest = new RegisterRequest();
                registerRequest.setUsername("ahmad.faiq41");
                registerRequest.setEmail("faiq@kampus.id");
                registerRequest.setDisplayName("Faiq");
                registerRequest.setPassword("rahasia");

                loginRequest = new LoginRequest();
                loginRequest.setUsername("ahmad.faiq41");
                loginRequest.setPassword("rahasia");

                user = new User();
                user.setId(UUID.randomUUID());
                user.setUsername("ahmad.faiq41");
        }

        @Test
        void register_Success() throws Exception {
                when(authService.register(any(RegisterRequest.class))).thenReturn(user);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.message").value("Registrasi berhasil"))
                                .andExpect(jsonPath("$.userId").value(user.getId().toString()));
        }

        @Test
        void register_Failure() throws Exception {
                when(authService.register(any(RegisterRequest.class)))
                                .thenThrow(new IllegalArgumentException("Username sudah terpakai!"));

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Username sudah terpakai!"));
        }

        @Test
        void login_Success() throws Exception {
                LoginResponse loginResponse = new LoginResponse(user, "dummy-jwt-token");

                when(authService.login(anyString(), anyString())).thenReturn(loginResponse);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Login berhasil"))
                                .andExpect(jsonPath("$.username").value("ahmad.faiq41"))
                                .andExpect(jsonPath("$.token").value("dummy-jwt-token"));
        }

        @Test
        void login_Failure() throws Exception {
                when(authService.login(anyString(), anyString()))
                                .thenThrow(new IllegalArgumentException("Username/email atau password salah!"));

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").value("Username/email atau password salah!"));
        }
}