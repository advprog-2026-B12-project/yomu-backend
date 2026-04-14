package id.ac.ui.cs.advprog.yomubackend.auth.dto;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private User user;
    private String token;
}
