package id.ac.ui.cs.advprog.yomubackend.shared.events.clan;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClanDeletedEvent(
        Long clanId,
        UUID leaderUserId,
        LocalDateTime timestamp
) {
}