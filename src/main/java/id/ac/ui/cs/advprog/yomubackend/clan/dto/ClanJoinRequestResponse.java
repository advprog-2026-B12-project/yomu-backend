package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ClanJoinRequestResponse {
    private Long id;
    private Long clanId;
    private String clanName;
    private UUID userId;
    private String status;
    private Instant requestedAt;
    private Instant resolvedAt;
}
