package id.ac.ui.cs.advprog.yomubackend.user.controller;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.user.dto.UpdateProfileRequest;
import id.ac.ui.cs.advprog.yomubackend.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .findFirst()
                    .orElse("Permintaan tidak valid");
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User updatedUser = userService.updateProfile(authentication.getName(), request);

            return ResponseEntity.ok(Map.of(
                    "message", "Profil berhasil diperbarui",
                    "userId", updatedUser.getId(),
                    "username", updatedUser.getUsername(),
                    "displayName", updatedUser.getDisplayName()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            userService.deleteAccount(authentication.getName());
            return ResponseEntity.ok(Map.of("message", "Akun berhasil dihapus"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
