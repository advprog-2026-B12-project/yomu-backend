package id.ac.ui.cs.advprog.yomubackend.shared.events.clan;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ClanPromotionEvent(
        UUID clanId,      
        List<UUID> memberIds,
        String newDivision,
        LocalDateTime timestamp
) {
}