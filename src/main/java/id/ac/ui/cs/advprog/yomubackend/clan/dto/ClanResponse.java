package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ClanResponse {
    private Long id;
    private String name;
    private String description;
    private UUID leaderUserId;
    private String division;
    private long memberCount;
    private Instant createdAt;
}