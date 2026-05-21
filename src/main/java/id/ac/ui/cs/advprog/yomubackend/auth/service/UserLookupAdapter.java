package id.ac.ui.cs.advprog.yomubackend.auth.service;

import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.discussion.service.UserLookup;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserLookupAdapter implements UserLookup {

    private final UserRepository userRepository;

    public UserLookupAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UUID resolveUserId(String username) {
        return userRepository.findByUsername(username)
                .map(user -> user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan!"));
    }
}
