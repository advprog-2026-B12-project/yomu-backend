package id.ac.ui.cs.advprog.yomubackend.clan.completion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ClanPromotion(
        Long clanId,
        List<UUID> memberIds,
        String newDivision,
        LocalDateTime promotedAt
) {}
