package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ApiMessageResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanMemberResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.service.ClanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    @Mock
    private ClanService clanService;

    private ClanController clanController;

    @BeforeEach
    void setUp() {
        clanController = new ClanController(clanService);
    }

    @Test
    void listClans_shouldReturnOkAndAllClans_whenClansExist() {
        ClanResponse clan1 = new ClanResponse(
                1L, "Alpha", "Alpha desc", LEADER_ID, "BRONZE", 3L,
                Instant.parse("2026-01-01T00:00:00Z")
        );
        ClanResponse clan2 = new ClanResponse(
                2L, "Beta", "Beta desc", MEMBER_ID, "SILVER", 5L,
                Instant.parse("2026-01-02T00:00:00Z")
        );

        when(clanService.getAllClans()).thenReturn(List.of(clan1, clan2));

        ResponseEntity<List<ClanResponse>> response = clanController.listClans();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(LEADER_ID, response.getBody().get(0).getLeaderUserId());
        assertEquals(MEMBER_ID, response.getBody().get(1).getLeaderUserId());

        verify(clanService).getAllClans();
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void listClans_shouldReturnOkAndEmptyList_whenNoClansExist() {
        when(clanService.getAllClans()).thenReturn(List.of());

        ResponseEntity<List<ClanResponse>> response = clanController.listClans();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(clanService).getAllClans();
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void getClanById_shouldReturnOkAndClan_whenClanExists() {
        ClanResponse clan = new ClanResponse(
                1L, "Warriors", "Fight together", LEADER_ID, "BRONZE", 4L,
                Instant.parse("2026-01-03T00:00:00Z")
        );

        when(clanService.getClanById(1L)).thenReturn(clan);

        ResponseEntity<ClanResponse> response = clanController.getClanById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Warriors", response.getBody().getName());
        assertEquals(LEADER_ID, response.getBody().getLeaderUserId());

        verify(clanService).getClanById(1L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void getClanById_shouldThrowException_whenServiceFails() {
        when(clanService.getClanById(99L)).thenThrow(new RuntimeException("Clan not found"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> clanController.getClanById(99L)
        );

        assertEquals("Clan not found", ex.getMessage());
        verify(clanService).getClanById(99L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void getMembers_shouldReturnOkAndMembers_whenMembersExist() {
        ClanMemberResponse member1 = new ClanMemberResponse(
                LEADER_ID, ClanMember.Role.LEADER, Instant.parse("2026-01-01T00:00:00Z")
        );
        ClanMemberResponse member2 = new ClanMemberResponse(
                MEMBER_ID, ClanMember.Role.MEMBER, Instant.parse("2026-01-02T00:00:00Z")
        );

        when(clanService.getMembers(1L)).thenReturn(List.of(member1, member2));

        ResponseEntity<List<ClanMemberResponse>> response = clanController.getMembers(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(LEADER_ID, response.getBody().get(0).getUserId());
        assertEquals(MEMBER_ID, response.getBody().get(1).getUserId());

        verify(clanService).getMembers(1L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void getMembers_shouldReturnOkAndEmptyList_whenClanHasNoMembers() {
        when(clanService.getMembers(1L)).thenReturn(List.of());

        ResponseEntity<List<ClanMemberResponse>> response = clanController.getMembers(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(clanService).getMembers(1L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void getMembers_shouldThrowException_whenServiceFails() {
        when(clanService.getMembers(99L)).thenThrow(new RuntimeException("Clan not found"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> clanController.getMembers(99L)
        );

        assertEquals("Clan not found", ex.getMessage());
        verify(clanService).getMembers(99L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void createClan_shouldUseAuthenticatedUserAndReturnCreatedClan_whenRequestIsValid() {
        User user = user(LEADER_ID);
        CreateClanRequest request = new CreateClanRequest();
        request.setName("Warriors");
        request.setDescription("Fight together");

        ClanResponse created = new ClanResponse(
                1L, "Warriors", "Fight together", LEADER_ID, "BRONZE", 1L,
                Instant.parse("2026-01-01T00:00:00Z")
        );

        when(clanService.createClan(LEADER_ID, "Warriors", "Fight together")).thenReturn(created);

        ResponseEntity<ClanResponse> response = clanController.createClan(user, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(LEADER_ID, response.getBody().getLeaderUserId());
        verify(clanService).createClan(LEADER_ID, "Warriors", "Fight together");
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void createClan_shouldStillCallService_whenDescriptionIsNull() {
        User user = user(MEMBER_ID);
        CreateClanRequest request = new CreateClanRequest();
        request.setName("Nameless");
        request.setDescription(null);

        ClanResponse created = new ClanResponse(
                3L, "Nameless", null, MEMBER_ID, "BRONZE", 1L,
                Instant.parse("2026-01-01T00:00:00Z")
        );

        when(clanService.createClan(MEMBER_ID, "Nameless", null)).thenReturn(created);

        ResponseEntity<ClanResponse> response = clanController.createClan(user, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getDescription());
        verify(clanService).createClan(MEMBER_ID, "Nameless", null);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void createClan_shouldThrowException_whenServiceFails() {
        User user = user(OTHER_ID);
        CreateClanRequest request = new CreateClanRequest();
        request.setName("Broken Clan");
        request.setDescription("desc");

        when(clanService.createClan(OTHER_ID, "Broken Clan", "desc"))
                .thenThrow(new RuntimeException("Failed to create clan"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> clanController.createClan(user, request)
        );

        assertEquals("Failed to create clan", ex.getMessage());
        verify(clanService).createClan(OTHER_ID, "Broken Clan", "desc");
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void joinClan_shouldUseAuthenticatedUserAndClanIdFromPathVariable() {
        User user = user(MEMBER_ID);
        ClanMemberResponse member = new ClanMemberResponse(
                MEMBER_ID, ClanMember.Role.MEMBER, Instant.parse("2026-01-05T00:00:00Z")
        );

        when(clanService.joinClan(MEMBER_ID, 5L)).thenReturn(member);

        ResponseEntity<ClanMemberResponse> response = clanController.joinClan(user, 5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(MEMBER_ID, response.getBody().getUserId());
        verify(clanService).joinClan(MEMBER_ID, 5L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void joinClan_shouldThrowException_whenServiceFails() {
        User user = user(MEMBER_ID);
        when(clanService.joinClan(MEMBER_ID, 5L))
                .thenThrow(new RuntimeException("User already joined a clan"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> clanController.joinClan(user, 5L)
        );

        assertEquals("User already joined a clan", ex.getMessage());
        verify(clanService).joinClan(MEMBER_ID, 5L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void leaveClan_shouldUseAuthenticatedUserAndReturnSuccessMessage() {
        User user = user(MEMBER_ID);

        ResponseEntity<ApiMessageResponse> response = clanController.leaveClan(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Successfully left the clan", response.getBody().getMessage());
        verify(clanService).leaveClan(MEMBER_ID);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void leaveClan_shouldThrowException_whenServiceFails() {
        User user = user(MEMBER_ID);
        doThrow(new RuntimeException("User is not in any clan"))
                .when(clanService).leaveClan(MEMBER_ID);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> clanController.leaveClan(user)
        );

        assertEquals("User is not in any clan", ex.getMessage());
        verify(clanService).leaveClan(MEMBER_ID);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void deleteClan_shouldUseAuthenticatedUserAndReturnSuccessMessage() {
        User user = user(LEADER_ID);

        ResponseEntity<ApiMessageResponse> response = clanController.deleteClan(7L, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Clan deleted successfully", response.getBody().getMessage());
        verify(clanService).deleteClan(LEADER_ID, 7L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void deleteClan_shouldThrowException_whenServiceFails() {
        User user = user(OTHER_ID);
        doThrow(new RuntimeException("Only leader can delete clan"))
                .when(clanService).deleteClan(OTHER_ID, 7L);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> clanController.deleteClan(7L, user)
        );

        assertEquals("Only leader can delete clan", ex.getMessage());
        verify(clanService).deleteClan(OTHER_ID, 7L);
        verifyNoMoreInteractions(clanService);
    }

    private User user(UUID userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }
}
