package id.ac.ui.cs.advprog.yomubackend.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleSsoResult {
    private boolean needsRegistration;
    // Populated when needsRegistration = false (existing user)
    private String message;
    private String token;
    private UserDto user;
    // Populated when needsRegistration = true (new user)
    private String email;
    private String googleName;
}
