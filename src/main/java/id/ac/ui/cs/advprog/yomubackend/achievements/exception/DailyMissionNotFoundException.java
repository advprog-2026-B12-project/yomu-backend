package id.ac.ui.cs.advprog.yomubackend.achievements.exception;

import java.util.UUID;

public class DailyMissionNotFoundException extends RuntimeException {
    public DailyMissionNotFoundException(UUID id) {
        super("Daily Mission tidak ditemukan dengan id: " + id);
    }
}
