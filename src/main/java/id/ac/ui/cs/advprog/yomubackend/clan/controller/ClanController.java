package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ApiMessageResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanJoinRequestResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanMemberResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.yomubackend.clan.service.ClanJoinRequestService;
import id.ac.ui.cs.advprog.yomubackend.clan.service.ClanManagementService;
import id.ac.ui.cs.advprog.yomubackend.clan.service.ClanMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clans")
@PreAuthorize("hasRole('PELAJAR') or hasRole('ADMIN')")
public class ClanController {

    private final ClanManagementService clanManagementService;
    private final ClanJoinRequestService clanJoinRequestService;
    private final ClanMemberService clanMemberService;

    public ClanController(ClanManagementService clanManagementService,
                          ClanJoinRequestService clanJoinRequestService,
                          ClanMemberService clanMemberService) {
        this.clanManagementService = clanManagementService;
        this.clanJoinRequestService = clanJoinRequestService;
        this.clanMemberService = clanMemberService;
    }

    @GetMapping
    public ResponseEntity<List<ClanResponse>> listClans() {
        return ResponseEntity.ok(clanManagementService.getAllClans());
    }

    @GetMapping("/me")
    public ResponseEntity<ClanResponse> getMyClan(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clanManagementService.getMyClan(user.getId()));
    }

    @GetMapping("/{clanId}")
    public ResponseEntity<ClanResponse> getClanById(@PathVariable Long clanId) {
        return ResponseEntity.ok(clanManagementService.getClanById(clanId));
    }

    @GetMapping("/{clanId}/members")
    public ResponseEntity<List<ClanMemberResponse>> getMembers(@PathVariable Long clanId) {
        return ResponseEntity.ok(clanManagementService.getMembers(clanId));
    }

    @PostMapping
    public ResponseEntity<ClanResponse> createClan(@AuthenticationPrincipal User user,
                                                   @RequestBody CreateClanRequest request) {
        return ResponseEntity.ok(
                clanManagementService.createClan(user.getId(), request.getName(), request.getDescription())
        );
    }

    @PostMapping("/{clanId}/join")
    public ResponseEntity<ClanJoinRequestResponse> requestToJoinClan(
            @AuthenticationPrincipal User user,
            @PathVariable Long clanId) {
        return ResponseEntity.ok(clanJoinRequestService.requestToJoinClan(user.getId(), clanId));
    }

    @GetMapping("/{clanId}/join-requests")
    public ResponseEntity<List<ClanJoinRequestResponse>> getPendingJoinRequests(
            @AuthenticationPrincipal User user,
            @PathVariable Long clanId) {
        return ResponseEntity.ok(clanJoinRequestService.getPendingJoinRequests(user.getId(), clanId));
    }

    @PostMapping("/join-requests/{requestId}/approve")
    public ResponseEntity<ClanMemberResponse> approveJoinRequest(
            @AuthenticationPrincipal User user,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(clanJoinRequestService.approveJoinRequest(user.getId(), requestId));
    }

    @PostMapping("/join-requests/{requestId}/reject")
    public ResponseEntity<ApiMessageResponse> rejectJoinRequest(
            @AuthenticationPrincipal User user,
            @PathVariable Long requestId) {
        clanJoinRequestService.rejectJoinRequest(user.getId(), requestId);
        return ResponseEntity.ok(new ApiMessageResponse("Clan join request rejected"));
    }

    @DeleteMapping("/leave")
    public ResponseEntity<ApiMessageResponse> leaveClan(@AuthenticationPrincipal User user) {
        clanMemberService.leaveClan(user.getId());
        return ResponseEntity.ok(new ApiMessageResponse("Successfully left the clan"));
    }

    @DeleteMapping("/{clanId}")
    public ResponseEntity<ApiMessageResponse> deleteClan(
            @PathVariable Long clanId,
            @AuthenticationPrincipal User user) {
        clanManagementService.deleteClan(user.getId(), clanId);
        return ResponseEntity.ok(new ApiMessageResponse("Clan deleted successfully"));
    }
}
