package id.ac.ui.cs.advprog.yomubackend.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
