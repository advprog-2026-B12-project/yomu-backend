package id.ac.ui.cs.advprog.yomubackend.auth.controller;

import tools.jackson.databind.json.JsonMapper;

import id.ac.ui.cs.advprog.yomubackend.auth.dto.GoogleSsoResult;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.GoogleTokenRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.LoginResponse;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.UserDto;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
        private UserDto userDto;

        @BeforeEach
        void setUp() {
                registerRequest = new RegisterRequest();
                registerRequest.setUsername("ahmadFaiq41");
                registerRequest.setEmail("faiq@kampus.id");
                registerRequest.setDisplayName("Faiq");
                registerRequest.setPassword("rahasia");

                loginRequest = new LoginRequest();
                loginRequest.setUsername("ahmadFaiq41");
                loginRequest.setPassword("rahasia");

                user = new User();
                user.setId(UUID.randomUUID());
                user.setUsername("ahmadFaiq41");
                user.setDisplayName("Faiq");
                user.setEmail("faiq@kampus.id");

                userDto = UserDto.builder()
                                .userId(user.getId())
                                .username("ahmadFaiq41")
                                .displayName("Faiq")
                                .email("faiq@kampus.id")
                                .build();
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
        void register_BlankRequiredFields_ReturnsBadRequest() throws Exception {
                RegisterRequest request = new RegisterRequest();
                request.setUsername("");
                request.setEmail("");
                request.setDisplayName("");
                request.setPassword("");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());

                verify(authService, never()).register(any(RegisterRequest.class));
        }

        @Test
        void register_InvalidEmail_ReturnsBadRequest() throws Exception {
                registerRequest.setEmail("not-an-email");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isBadRequest());

                verify(authService, never()).register(any(RegisterRequest.class));
        }

        @Test
        void login_Success() throws Exception {
                LoginResponse loginResponse = LoginResponse.builder()
                                .message("Login berhasil")
                                .token("dummy-jwt-token")
                                .user(userDto)
                                .build();

                when(authService.login(anyString(), anyString())).thenReturn(loginResponse);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Login berhasil"))
                                .andExpect(jsonPath("$.token").value("dummy-jwt-token"))
                                .andExpect(jsonPath("$.user.username").value("ahmadFaiq41"))
                                .andExpect(jsonPath("$.user.displayName").value("Faiq"))
                                .andExpect(jsonPath("$.user.email").value("faiq@kampus.id"))
                                .andExpect(jsonPath("$.user.userId").value(user.getId().toString()));
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

        @Test
        void login_BlankRequiredFields_ReturnsBadRequest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("");
                request.setPassword("");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());

                verify(authService, never()).login(anyString(), anyString());
        }

        @Test
        void googleLogin_ExistingUser_Success() throws Exception {
                GoogleSsoResult result = GoogleSsoResult.builder()
                                .needsRegistration(false)
                                .message("Login berhasil")
                                .token("jwt-token")
                                .user(userDto)
                                .build();

                when(authService.googleLogin(anyString())).thenReturn(result);

                GoogleTokenRequest request = new GoogleTokenRequest();
                request.setToken("valid-google-token");

                mockMvc.perform(post("/api/auth/google")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Login berhasil"))
                                .andExpect(jsonPath("$.token").value("jwt-token"))
                                .andExpect(jsonPath("$.user.username").value("ahmadFaiq41"))
                                .andExpect(jsonPath("$.user.displayName").value("Faiq"))
                                .andExpect(jsonPath("$.user.email").value("faiq@kampus.id"))
                                .andExpect(jsonPath("$.user.userId").value(user.getId().toString()));
        }

        @Test
        void googleLogin_NewUser_NeedsRegistration() throws Exception {
                GoogleSsoResult result = GoogleSsoResult.builder()
                                .needsRegistration(true)
                                .email("new@kampus.id")
                                .googleName("New User")
                                .build();

                when(authService.googleLogin(anyString())).thenReturn(result);

                GoogleTokenRequest request = new GoogleTokenRequest();
                request.setToken("valid-google-token");

                mockMvc.perform(post("/api/auth/google")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.needsRegistration").value(true))
                                .andExpect(jsonPath("$.email").value("new@kampus.id"))
                                .andExpect(jsonPath("$.googleName").value("New User"));
        }

        @Test
        void googleLogin_NewUser_NullGoogleName_NeedsRegistration() throws Exception {
                GoogleSsoResult result = GoogleSsoResult.builder()
                                .needsRegistration(true)
                                .email("new@kampus.id")
                                .googleName(null)
                                .build();

                when(authService.googleLogin(anyString())).thenReturn(result);

                GoogleTokenRequest request = new GoogleTokenRequest();
                request.setToken("valid-google-token");

                mockMvc.perform(post("/api/auth/google")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.needsRegistration").value(true))
                                .andExpect(jsonPath("$.email").value("new@kampus.id"));
        }

        @Test
        void googleLogin_BlankToken_ReturnsBadRequest() throws Exception {
                GoogleTokenRequest request = new GoogleTokenRequest();
                request.setToken("");

                mockMvc.perform(post("/api/auth/google")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());

                verify(authService, never()).googleLogin(anyString());
        }

        @Test
        void googleLogin_InvalidToken_BadRequest() throws Exception {
                when(authService.googleLogin(anyString()))
                                .thenThrow(new IllegalArgumentException("Token Google tidak valid!"));

                GoogleTokenRequest request = new GoogleTokenRequest();
                request.setToken("bad-token");

                mockMvc.perform(post("/api/auth/google")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Token Google tidak valid!"));
        }
}
