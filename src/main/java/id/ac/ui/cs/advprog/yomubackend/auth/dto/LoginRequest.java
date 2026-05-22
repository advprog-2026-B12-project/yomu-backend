package id.ac.ui.cs.advprog.yomubackend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username/email wajib diisi!")
    private String username;

    @NotBlank(message = "Password wajib diisi!")
    private String password;
}
