package id.ac.ui.cs.advprog.yomubackend.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.LoginResponse;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.GoogleSsoResult;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.UserDto;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           JwtService jwtService, GoogleIdTokenVerifier googleIdTokenVerifier) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
    }

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username sudah terpakai!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email sudah terdaftar!");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setDisplayName(request.getDisplayName());

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        newUser.setPassword(hashedPassword);

        return userRepository.save(newUser);
    }

    @Override
    public LoginResponse login(String usernameOrEmail, String password) {
        Optional<User> optionalUser;
        if (usernameOrEmail.contains("@")) {
            optionalUser = userRepository.findByEmail(usernameOrEmail);
        } else {
            optionalUser = userRepository.findByUsername(usernameOrEmail);
        }

        User user = optionalUser
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("Username/email atau password salah!"));

        String token = jwtService.generateToken(Map.of("role", "ROLE_" + user.getRole().name()), user.getUsername());
        UserDto userDto = buildUserDto(user);
        return LoginResponse.builder()
                .message("Login berhasil")
                .token(token)
                .user(userDto)
                .build();
    }

    @Override
    public GoogleSsoResult googleLogin(String idToken) {
        try {
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
            if (googleIdToken == null) {
                throw new IllegalArgumentException("Token Google tidak valid!");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            String googleName = (String) payload.get("name");

            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                User found = existingUser.get();
                String jwtToken = jwtService.generateToken(Map.of("role", "ROLE_" + found.getRole().name()), found.getUsername());
                return GoogleSsoResult.builder()
                        .needsRegistration(false)
                        .message("Login berhasil")
                        .token(jwtToken)
                        .user(buildUserDto(found))
                        .build();
            } else {
                return GoogleSsoResult.builder()
                        .needsRegistration(true)
                        .email(email)
                        .googleName(googleName)
                        .build();
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Token Google tidak valid!", e);
        }
    }

    private UserDto buildUserDto(User user) {
        return UserDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
