package id.ac.ui.cs.advprog.yomubackend.auth.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserLoggedInEvent {
    private final UUID userId;
    private final LocalDateTime timestamp;

    public UserLoggedInEvent(UUID userId, LocalDateTime timestamp) {
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public UUID getUserId() { return userId; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
