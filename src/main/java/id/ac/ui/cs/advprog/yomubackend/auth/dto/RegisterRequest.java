package id.ac.ui.cs.advprog.yomubackend.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String displayName;
    private String password;
}