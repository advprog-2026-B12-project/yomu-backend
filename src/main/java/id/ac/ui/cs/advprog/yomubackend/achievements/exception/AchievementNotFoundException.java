package id.ac.ui.cs.advprog.yomubackend.achievements.exception;

import java.util.UUID;

public class AchievementNotFoundException extends RuntimeException {
    public AchievementNotFoundException(UUID id) {
        super("Achievement tidak ditemukan dengan id: " + id);
    }
}
