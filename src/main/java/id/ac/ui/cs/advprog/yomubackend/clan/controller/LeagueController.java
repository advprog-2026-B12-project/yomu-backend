package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ApiMessageResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.service.LeagueService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/leaderboard/me")
    @PreAuthorize("hasRole('PELAJAR')")
    public ResponseEntity<List<LeaderboardEntryResponse>> getMyLeaderboard(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(leagueService.getLeaderboardForUser(user.getId()));
    }

    @PostMapping("/season/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiMessageResponse> triggerSeasonReset() {
        leagueService.triggerSeasonReset();
        return ResponseEntity.ok(new ApiMessageResponse("Season reset triggered"));
    }
}
