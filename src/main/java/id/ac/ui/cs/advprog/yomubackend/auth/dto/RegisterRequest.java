package id.ac.ui.cs.advprog.yomubackend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username wajib diisi!")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username hanya boleh mengandung huruf dan angka!")
    private String username;

    @NotBlank(message = "Email wajib diisi!")
    @Email(message = "Format email tidak valid!")
    private String email;

    @NotBlank(message = "Display name wajib diisi!")
    private String displayName;

    @NotBlank(message = "Password wajib diisi!")
    private String password;
}
