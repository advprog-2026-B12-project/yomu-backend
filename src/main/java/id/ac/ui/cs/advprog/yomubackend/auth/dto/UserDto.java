package id.ac.ui.cs.advprog.yomubackend.auth.dto;

import id.ac.ui.cs.advprog.yomubackend.auth.model.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserDto {
    private UUID userId;
    private String username;
    private String displayName;
    private String email;
    private Role role;
}
