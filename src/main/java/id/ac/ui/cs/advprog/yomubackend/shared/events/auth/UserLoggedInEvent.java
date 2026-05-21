package id.ac.ui.cs.advprog.yomubackend.shared.events.auth;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserLoggedInEvent(
    UUID userId,
    LocalDateTime timestamp
) {
}