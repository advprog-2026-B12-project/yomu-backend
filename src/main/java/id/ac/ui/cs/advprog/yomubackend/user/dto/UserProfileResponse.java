package id.ac.ui.cs.advprog.yomubackend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private UUID userId;
    private String username;
    private String displayName;
}
