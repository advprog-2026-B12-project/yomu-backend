package id.ac.ui.cs.advprog.yomubackend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleTokenRequest {
    @NotBlank(message = "Token Google wajib diisi!")
    private String token;
}
