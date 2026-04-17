package id.ac.ui.cs.advprog.yomubackend.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String displayName;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username hanya boleh mengandung huruf dan angka!")
    private String username;

    private String oldPassword;
    private String newPassword;
}
