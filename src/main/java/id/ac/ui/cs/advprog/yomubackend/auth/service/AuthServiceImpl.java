package id.ac.ui.cs.advprog.yomubackend.auth.service;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.LoginResponse;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username sudah terpakai!");
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
                
        String token = jwtService.generateToken(user.getUsername());
        return new LoginResponse(user, token);
    }
}