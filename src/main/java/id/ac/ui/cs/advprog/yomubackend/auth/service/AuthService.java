package id.ac.ui.cs.advprog.yomubackend.auth.service;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.LoginResponse;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.GoogleSsoResult;

public interface AuthService {
    User register(RegisterRequest request);
    LoginResponse login(String username, String password);
    GoogleSsoResult googleLogin(String idToken);
}
