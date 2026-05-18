package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ApiMessageResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanJoinRequestResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanMemberResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.service.ClanJoinRequestService;
import id.ac.ui.cs.advprog.yomubackend.clan.service.ClanManagementService;
import id.ac.ui.cs.advprog.yomubackend.clan.service.ClanMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanControllerTest {

    private static final UUID LEADER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID OTHER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @Mock private ClanManagementService clanManagementService;
    @Mock private ClanJoinRequestService clanJoinRequestService;
    @Mock private ClanMemberService clanMemberService;

    private ClanController clanController;

    @BeforeEach
    void setUp() {
        clanController = new ClanController(clanManagementService, clanJoinRequestService, clanMemberService);
    }

    @Test
    void controller_shouldRequirePelajarRole() {
        PreAuthorize preAuthorize = ClanController.class.getAnnotation(PreAuthorize.class);

        assertNotNull(preAuthorize);
        assertEquals("hasRole('PELAJAR')", preAuthorize.value());
    }

    @Test
    void listClans_shouldReturnOkAndAllClans_whenClansExist() {
        ClanResponse clan1 = new ClanResponse(1L, "Alpha", "Alpha desc", LEADER_ID, "BRONZE", 3L,
                Instant.parse("2026-01-01T00:00:00Z"));
        ClanResponse clan2 = new ClanResponse(2L, "Beta", "Beta desc", MEMBER_ID, "SILVER", 5L,
                Instant.parse("2026-01-02T00:00:00Z"));

        when(clanManagementService.getAllClans()).thenReturn(List.of(clan1, clan2));

        ResponseEntity<List<ClanResponse>> response = clanController.listClans();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(LEADER_ID, response.getBody().get(0).getLeaderUserId());
        verify(clanManagementService).getAllClans();
        verifyNoMoreInteractions(clanManagementService, clanJoinRequestService, clanMemberService);
    }

    @Test
    void listClans_shouldReturnOkAndEmptyList_whenNoClansExist() {
        when(clanManagementService.getAllClans()).thenReturn(List.of());

        ResponseEntity<List<ClanResponse>> response = clanController.listClans();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(clanManagementService).getAllClans();
    }

    @Test
    void getClanById_shouldReturnOkAndClan_whenClanExists() {
        ClanResponse clan = new ClanResponse(1L, "Warriors", "Fight together", LEADER_ID, "BRONZE", 4L,
                Instant.parse("2026-01-03T00:00:00Z"));

        when(clanManagementService.getClanById(1L)).thenReturn(clan);

        ResponseEntity<ClanResponse> response = clanController.getClanById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Warriors", response.getBody().getName());
        verify(clanManagementService).getClanById(1L);
    }

    @Test
    void getClanById_shouldThrowException_whenServiceFails() {
        when(clanManagementService.getClanById(99L)).thenThrow(new RuntimeException("Clan not found"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clanController.getClanById(99L));

        assertEquals("Clan not found", ex.getMessage());
    }

    @Test
    void getMembers_shouldReturnOkAndMembers_whenMembersExist() {
        ClanMemberResponse member1 = new ClanMemberResponse(LEADER_ID, ClanMember.Role.LEADER,
                Instant.parse("2026-01-01T00:00:00Z"));
        ClanMemberResponse member2 = new ClanMemberResponse(MEMBER_ID, ClanMember.Role.MEMBER,
                Instant.parse("2026-01-02T00:00:00Z"));

        when(clanManagementService.getMembers(1L)).thenReturn(List.of(member1, member2));

        ResponseEntity<List<ClanMemberResponse>> response = clanController.getMembers(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(LEADER_ID, response.getBody().get(0).getUserId());
        verify(clanManagementService).getMembers(1L);
    }

    @Test
    void getMembers_shouldReturnOkAndEmptyList_whenClanHasNoMembers() {
        when(clanManagementService.getMembers(1L)).thenReturn(List.of());

        ResponseEntity<List<ClanMemberResponse>> response = clanController.getMembers(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void createClan_shouldUseAuthenticatedUserAndReturnCreatedClan_whenRequestIsValid() {
        User user = user(LEADER_ID);
        CreateClanRequest request = new CreateClanRequest();
        request.setName("Warriors");
        request.setDescription("Fight together");

        ClanResponse created = new ClanResponse(1L, "Warriors", "Fight together", LEADER_ID, "BRONZE", 1L,
                Instant.parse("2026-01-01T00:00:00Z"));

        when(clanManagementService.createClan(LEADER_ID, "Warriors", "Fight together")).thenReturn(created);

        ResponseEntity<ClanResponse> response = clanController.createClan(user, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(LEADER_ID, response.getBody().getLeaderUserId());
        verify(clanManagementService).createClan(LEADER_ID, "Warriors", "Fight together");
    }

    @Test
    void createClan_shouldStillCallService_whenDescriptionIsNull() {
        User user = user(MEMBER_ID);
        CreateClanRequest request = new CreateClanRequest();
        request.setName("Nameless");

        ClanResponse created = new ClanResponse(3L, "Nameless", null, MEMBER_ID, "BRONZE", 1L,
                Instant.parse("2026-01-01T00:00:00Z"));

        when(clanManagementService.createClan(MEMBER_ID, "Nameless", null)).thenReturn(created);

        ResponseEntity<ClanResponse> response = clanController.createClan(user, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().getDescription());
    }

    @Test
    void requestToJoinClan_shouldUseAuthenticatedUserAndClanIdFromPathVariable() {
        User user = user(MEMBER_ID);
        ClanJoinRequestResponse joinResponse = new ClanJoinRequestResponse(
                10L, 5L, "Warriors", MEMBER_ID, "PENDING",
                Instant.parse("2026-01-05T00:00:00Z"), null);

        when(clanJoinRequestService.requestToJoinClan(MEMBER_ID, 5L)).thenReturn(joinResponse);

        ResponseEntity<ClanJoinRequestResponse> response = clanController.requestToJoinClan(user, 5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
        assertEquals(MEMBER_ID, response.getBody().getUserId());
        verify(clanJoinRequestService).requestToJoinClan(MEMBER_ID, 5L);
    }

    @Test
    void getPendingJoinRequests_shouldUseAuthenticatedLeaderAndClanId() {
        User user = user(LEADER_ID);
        ClanJoinRequestResponse joinResponse = new ClanJoinRequestResponse(
                10L, 5L, "Warriors", MEMBER_ID, "PENDING",
                Instant.parse("2026-01-05T00:00:00Z"), null);

        when(clanJoinRequestService.getPendingJoinRequests(LEADER_ID, 5L)).thenReturn(List.of(joinResponse));

        ResponseEntity<List<ClanJoinRequestResponse>> response = clanController.getPendingJoinRequests(user, 5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(10L, response.getBody().get(0).getId());
        verify(clanJoinRequestService).getPendingJoinRequests(LEADER_ID, 5L);
    }

    @Test
    void approveJoinRequest_shouldUseAuthenticatedLeaderAndReturnMember() {
        User user = user(LEADER_ID);
        ClanMemberResponse member = new ClanMemberResponse(MEMBER_ID, ClanMember.Role.MEMBER,
                Instant.parse("2026-01-05T00:00:00Z"));

        when(clanJoinRequestService.approveJoinRequest(LEADER_ID, 10L)).thenReturn(member);

        ResponseEntity<ClanMemberResponse> response = clanController.approveJoinRequest(user, 10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MEMBER_ID, response.getBody().getUserId());
        verify(clanJoinRequestService).approveJoinRequest(LEADER_ID, 10L);
    }

    @Test
    void rejectJoinRequest_shouldUseAuthenticatedLeaderAndReturnSuccessMessage() {
        User user = user(LEADER_ID);

        ResponseEntity<ApiMessageResponse> response = clanController.rejectJoinRequest(user, 10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Clan join request rejected", response.getBody().getMessage());
        verify(clanJoinRequestService).rejectJoinRequest(LEADER_ID, 10L);
    }

    @Test
    void leaveClan_shouldUseAuthenticatedUserAndReturnSuccessMessage() {
        User user = user(MEMBER_ID);

        ResponseEntity<ApiMessageResponse> response = clanController.leaveClan(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully left the clan", response.getBody().getMessage());
        verify(clanMemberService).leaveClan(MEMBER_ID);
    }

    @Test
    void leaveClan_shouldThrowException_whenServiceFails() {
        User user = user(MEMBER_ID);
        doThrow(new RuntimeException("User is not in any clan")).when(clanMemberService).leaveClan(MEMBER_ID);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clanController.leaveClan(user));

        assertEquals("User is not in any clan", ex.getMessage());
    }

    @Test
    void deleteClan_shouldUseAuthenticatedUserAndReturnSuccessMessage() {
        User user = user(LEADER_ID);

        ResponseEntity<ApiMessageResponse> response = clanController.deleteClan(7L, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Clan deleted successfully", response.getBody().getMessage());
        verify(clanManagementService).deleteClan(LEADER_ID, 7L);
    }

    @Test
    void deleteClan_shouldThrowException_whenServiceFails() {
        User user = user(OTHER_ID);
        doThrow(new RuntimeException("Only leader can delete clan"))
                .when(clanManagementService).deleteClan(OTHER_ID, 7L);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clanController.deleteClan(7L, user));

        assertEquals("Only leader can delete clan", ex.getMessage());
    }

    private User user(UUID userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }
}
