package id.ac.ui.cs.advprog.yomubackend.achievements.exception;

import java.util.UUID;

public class UserAchievementNotFoundException extends RuntimeException {
    public UserAchievementNotFoundException(UUID id) {
        super("User Achievement tidak ditemukan dengan id: " + id);
    }
}
