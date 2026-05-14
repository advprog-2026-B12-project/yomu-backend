package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ClanResponse {
    private Long id;
    private String name;
    private String description;
    private Long leaderUserId;
    private String division;
    private long memberCount;
    private Instant createdAt;
}