package id.ac.ui.cs.advprog.yomubackend.auth.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username hanya boleh mengandung huruf dan angka!")
    private String username;
    private String email;
    private String displayName;
    private String password;
}