package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.LeaderboardEntryResponse;

import java.util.List;
import java.util.UUID;

public interface LeagueService {
    List<LeaderboardEntryResponse> getLeaderboardByDivision(String division);
    List<LeaderboardEntryResponse> getLeaderboardForUser(UUID userId);
    void triggerSeasonReset();
}
