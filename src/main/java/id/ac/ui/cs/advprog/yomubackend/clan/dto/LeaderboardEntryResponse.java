package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardEntryResponse {
    private int rank;
    private Long clanId;
    private String clanName;
    private String division;
    private long memberCount;
    private int score;
}