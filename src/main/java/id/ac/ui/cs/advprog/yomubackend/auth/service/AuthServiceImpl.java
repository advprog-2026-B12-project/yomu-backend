package id.ac.ui.cs.advprog.yomubackend.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import id.ac.ui.cs.advprog.yomubackend.auth.event.UserLoggedInEvent;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.LoginResponse;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.GoogleSsoResult;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.UserDto;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.security.JwtService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final MeterRegistry meterRegistry;
    private final ApplicationEventPublisher eventPublisher;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           JwtService jwtService, GoogleIdTokenVerifier googleIdTokenVerifier,
                           MeterRegistry meterRegistry, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.meterRegistry = meterRegistry;
        this.eventPublisher = eventPublisher;
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

        User saved = userRepository.save(newUser);
        meterRegistry.counter("auth.register.success").increment();
        return saved;
    }

    @Override
    public LoginResponse login(String usernameOrEmail, String password) {
        Optional<User> optionalUser;
        if (usernameOrEmail.contains("@")) {
            optionalUser = userRepository.findByEmail(usernameOrEmail);
        } else {
            optionalUser = userRepository.findByUsername(usernameOrEmail);
        }

        try {
            User user = optionalUser
                    .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                    .orElseThrow(() -> new IllegalArgumentException("Username/email atau password salah!"));

            String token = generateTokenFor(user);
            UserDto userDto = buildUserDto(user);
            meterRegistry.counter("auth.login.success").increment();
            eventPublisher.publishEvent(new UserLoggedInEvent(user.getId(), LocalDateTime.now()));
            return LoginResponse.builder()
                    .message("Login berhasil")
                    .token(token)
                    .user(userDto)
                    .build();
        } catch (IllegalArgumentException e) {
            meterRegistry.counter("auth.login.failure").increment();
            throw e;
        }
    }

    @Override
    public GoogleSsoResult googleLogin(String idToken) {
        GoogleIdToken.Payload payload = verifyGoogleToken(idToken).getPayload();
        String email = payload.getEmail();
        String googleName = (String) payload.get("name");

        return userRepository.findByEmail(email)
                .map(this::buildGoogleLoginResponse)
                .orElseGet(() -> buildNeedsRegistrationResult(email, googleName));
    }

    private GoogleIdToken verifyGoogleToken(String idToken) {
        try {
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
            if (googleIdToken == null) {
                throw new IllegalArgumentException("Token Google tidak valid!");
            }
            return googleIdToken;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Token Google tidak valid!", e);
        }
    }

    private GoogleSsoResult buildGoogleLoginResponse(User user) {
        eventPublisher.publishEvent(new UserLoggedInEvent(user.getId(), LocalDateTime.now()));
        return GoogleSsoResult.builder()
                .needsRegistration(false)
                .message("Login berhasil")
                .token(generateTokenFor(user))
                .user(buildUserDto(user))
                .build();
    }

    private GoogleSsoResult buildNeedsRegistrationResult(String email, String googleName) {
        return GoogleSsoResult.builder()
                .needsRegistration(true)
                .email(email)
                .googleName(googleName)
                .build();
    }

    private String generateTokenFor(User user) {
        return jwtService.generateToken(Map.of("role", buildRoleClaim(user)), user.getUsername());
    }

    private String buildRoleClaim(User user) {
        return "ROLE_" + user.getRole().name();
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
