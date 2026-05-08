package id.ac.ui.cs.advprog.yomubackend.user.service;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.user.dto.UpdateProfileRequest;

public interface UserService {
    User updateProfile(String currentUsername, UpdateProfileRequest request);
    void deleteAccount(String username);
}
