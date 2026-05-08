package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.*;
import id.ac.ui.cs.advprog.yomubackend.clan.service.ClanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.util.List;

@RestController
@RequestMapping("/api/clans")
public class ClanController {

    private final ClanService clanService;

    public ClanController(ClanService clanService) {
        this.clanService = clanService;
    }

    @GetMapping
    public ResponseEntity<List<ClanResponse>> listClans() {
        return ResponseEntity.ok(clanService.getAllClans());
    }

    @GetMapping("/{clanId}")
    public ResponseEntity<ClanResponse> getClanById(@PathVariable Long clanId) {
        return ResponseEntity.ok(clanService.getClanById(clanId));
    }

    @GetMapping("/{clanId}/members")
    public ResponseEntity<List<ClanMemberResponse>> getMembers(@PathVariable Long clanId) {
        return ResponseEntity.ok(clanService.getMembers(clanId));
    }

    @PostMapping
    public ResponseEntity<ClanResponse> createClan(@AuthenticationPrincipal User user, @RequestBody CreateClanRequest request) {
        return ResponseEntity.ok(
                clanService.createClan(
                        user.getId(),
                        request.getName(),
                        request.getDescription()
                )
        );
    }

    @PostMapping("/{clanId}/join")
    public ResponseEntity<ClanMemberResponse> joinClan(
            @AuthenticationPrincipal User user,
            @PathVariable Long clanId) {
        return ResponseEntity.ok(
                clanService.joinClan(user.getId(), clanId)
        );
    }

    @DeleteMapping("/leave")
    public ResponseEntity<ApiMessageResponse> leaveClan(@AuthenticationPrincipal User user) {
        clanService.leaveClan(user.getId());
        return ResponseEntity.ok(new ApiMessageResponse("Successfully left the clan"));
    }

    @DeleteMapping("/{clanId}")
    public ResponseEntity<ApiMessageResponse> deleteClan(
            @PathVariable Long clanId,
            @AuthenticationPrincipal User user) {
        clanService.deleteClan(user.getId(), clanId);
        return ResponseEntity.ok(new ApiMessageResponse("Clan deleted successfully"));
    }
}