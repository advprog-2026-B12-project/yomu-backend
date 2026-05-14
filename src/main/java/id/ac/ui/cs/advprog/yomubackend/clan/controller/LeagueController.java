package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ApiMessageResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.service.LeagueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.List;

@RestController
@RequestMapping("/api/league")
public class LeagueController {

    private final LeagueService leagueService;

    public LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryResponse>> getLeaderboard(
            @RequestParam String division) {
        return ResponseEntity.ok(leagueService.getLeaderboardByDivision(division));
    }

    @PostMapping("/season/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiMessageResponse> triggerSeasonReset() {
        leagueService.triggerSeasonReset();
        return ResponseEntity.ok(new ApiMessageResponse("Season reset triggered"));
    }
}