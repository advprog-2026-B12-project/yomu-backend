package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ApiMessageResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanMemberResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.JoinClanRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanControllerTest {

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
                1L, "Alpha", "Alpha desc", 10L, "BRONZE", 3L,
                Instant.parse("2026-01-01T00:00:00Z")
        );
        ClanResponse clan2 = new ClanResponse(
                2L, "Beta", "Beta desc", 20L, "SILVER", 5L,
                Instant.parse("2026-01-02T00:00:00Z")
        );

        when(clanService.getAllClans()).thenReturn(List.of(clan1, clan2));

        ResponseEntity<List<ClanResponse>> response = clanController.listClans();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals("Alpha", response.getBody().get(0).getName());
        assertEquals(10L, response.getBody().get(0).getLeaderUserId());

        assertEquals(2L, response.getBody().get(1).getId());
        assertEquals("Beta", response.getBody().get(1).getName());
        assertEquals(20L, response.getBody().get(1).getLeaderUserId());

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
                1L, "Warriors", "Fight together", 42L, "BRONZE", 4L,
                Instant.parse("2026-01-03T00:00:00Z")
        );

        when(clanService.getClanById(1L)).thenReturn(clan);

        ResponseEntity<ClanResponse> response = clanController.getClanById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Warriors", response.getBody().getName());
        assertEquals(42L, response.getBody().getLeaderUserId());

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
                42L, ClanMember.Role.LEADER, Instant.parse("2026-01-01T00:00:00Z")
        );
        ClanMemberResponse member2 = new ClanMemberResponse(
                7L, ClanMember.Role.MEMBER, Instant.parse("2026-01-02T00:00:00Z")
        );

        when(clanService.getMembers(1L)).thenReturn(List.of(member1, member2));

        ResponseEntity<List<ClanMemberResponse>> response = clanController.getMembers(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        assertEquals(42L, response.getBody().get(0).getUserId());
        assertEquals(ClanMember.Role.LEADER, response.getBody().get(0).getRole());

        assertEquals(7L, response.getBody().get(1).getUserId());
        assertEquals(ClanMember.Role.MEMBER, response.getBody().get(1).getRole());

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
    void createClan_shouldReturnOkAndCreatedClan_whenRequestIsValid() {
        CreateClanRequest request = new CreateClanRequest();
        request.setUserId(42L);
        request.setName("Warriors");
        request.setDescription("Fight together");

        ClanResponse created = new ClanResponse(
                1L, "Warriors", "Fight together", 42L, "BRONZE", 1L,
                Instant.parse("2026-01-01T00:00:00Z")
        );

        when(clanService.createClan(42L, "Warriors", "Fight together")).thenReturn(created);

        ResponseEntity<ClanResponse> response = clanController.createClan(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Warriors", response.getBody().getName());
        assertEquals("Fight together", response.getBody().getDescription());
        assertEquals(42L, response.getBody().getLeaderUserId());

        verify(clanService).createClan(42L, "Warriors", "Fight together");
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void createClan_shouldStillCallService_whenDescriptionIsNull() {
        CreateClanRequest request = new CreateClanRequest();
        request.setUserId(1L);
        request.setName("Nameless");
        request.setDescription(null);

        ClanResponse created = new ClanResponse(
                3L, "Nameless", null, 1L, "BRONZE", 1L,
                Instant.parse("2026-01-01T00:00:00Z")
        );

        when(clanService.createClan(1L, "Nameless", null)).thenReturn(created);

        ResponseEntity<ClanResponse> response = clanController.createClan(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nameless", response.getBody().getName());
        assertNull(response.getBody().getDescription());

        verify(clanService).createClan(1L, "Nameless", null);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void createClan_shouldPassExactlyTheFieldsFromRequestToService() {
        CreateClanRequest request = new CreateClanRequest();
        request.setUserId(99L);
        request.setName("Rangers");
        request.setDescription("Scout ahead");

        ClanResponse created = new ClanResponse(
                5L, "Rangers", "Scout ahead", 99L, "BRONZE", 1L,
                Instant.parse("2026-01-01T00:00:00Z")
        );

        when(clanService.createClan(99L, "Rangers", "Scout ahead")).thenReturn(created);

        ResponseEntity<ClanResponse> response = clanController.createClan(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(clanService).createClan(99L, "Rangers", "Scout ahead");
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void createClan_shouldThrowException_whenServiceFails() {
        CreateClanRequest request = new CreateClanRequest();
        request.setUserId(7L);
        request.setName("Broken Clan");
        request.setDescription("desc");

        when(clanService.createClan(7L, "Broken Clan", "desc"))
                .thenThrow(new RuntimeException("Failed to create clan"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> clanController.createClan(request)
        );

        assertEquals("Failed to create clan", ex.getMessage());
        verify(clanService).createClan(7L, "Broken Clan", "desc");
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void joinClan_shouldReturnOkAndClanMember_whenRequestIsValid() {
        JoinClanRequest request = new JoinClanRequest();
        request.setUserId(20L);

        ClanMemberResponse member = new ClanMemberResponse(
                20L, ClanMember.Role.MEMBER, Instant.parse("2026-01-05T00:00:00Z")
        );

        when(clanService.joinClan(20L, 5L)).thenReturn(member);

        ResponseEntity<ClanMemberResponse> response = clanController.joinClan(5L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(20L, response.getBody().getUserId());
        assertEquals(ClanMember.Role.MEMBER, response.getBody().getRole());

        verify(clanService).joinClan(20L, 5L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void joinClan_shouldUseClanIdFromPathVariable() {
        JoinClanRequest request = new JoinClanRequest();
        request.setUserId(3L);

        ClanMemberResponse member = new ClanMemberResponse(
                3L, ClanMember.Role.MEMBER, Instant.parse("2026-01-05T00:00:00Z")
        );

        when(clanService.joinClan(3L, 99L)).thenReturn(member);

        ResponseEntity<ClanMemberResponse> response = clanController.joinClan(99L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(clanService).joinClan(3L, 99L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void joinClan_shouldCallServiceForDifferentUsers() {
        JoinClanRequest request1 = new JoinClanRequest();
        request1.setUserId(1L);

        JoinClanRequest request2 = new JoinClanRequest();
        request2.setUserId(2L);

        ClanMemberResponse member1 = new ClanMemberResponse(
                1L, ClanMember.Role.MEMBER, Instant.parse("2026-01-05T00:00:00Z")
        );
        ClanMemberResponse member2 = new ClanMemberResponse(
                2L, ClanMember.Role.MEMBER, Instant.parse("2026-01-06T00:00:00Z")
        );

        when(clanService.joinClan(1L, 5L)).thenReturn(member1);
        when(clanService.joinClan(2L, 5L)).thenReturn(member2);

        ResponseEntity<ClanMemberResponse> response1 = clanController.joinClan(5L, request1);
        ResponseEntity<ClanMemberResponse> response2 = clanController.joinClan(5L, request2);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(1L, response1.getBody().getUserId());
        assertEquals(2L, response2.getBody().getUserId());

        verify(clanService).joinClan(1L, 5L);
        verify(clanService).joinClan(2L, 5L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void joinClan_shouldThrowException_whenServiceFails() {
        JoinClanRequest request = new JoinClanRequest();
        request.setUserId(20L);

        when(clanService.joinClan(20L, 5L))
                .thenThrow(new RuntimeException("User already joined a clan"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> clanController.joinClan(5L, request)
        );

        assertEquals("User already joined a clan", ex.getMessage());
        verify(clanService).joinClan(20L, 5L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void leaveClan_shouldReturnSuccessMessage_whenRequestIsValid() {
        ResponseEntity<ApiMessageResponse> response = clanController.leaveClan(30L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Successfully left the clan", response.getBody().getMessage());

        verify(clanService).leaveClan(30L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void leaveClan_shouldThrowException_whenServiceFails() {
        doThrow(new RuntimeException("User is not in any clan"))
                .when(clanService).leaveClan(30L);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> clanController.leaveClan(30L)
        );

        assertEquals("User is not in any clan", ex.getMessage());
        verify(clanService).leaveClan(30L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void deleteClan_shouldReturnSuccessMessage_whenRequesterIsValid() {
        ResponseEntity<ApiMessageResponse> response = clanController.deleteClan(7L, 99L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Clan deleted successfully", response.getBody().getMessage());

        verify(clanService).deleteClan(99L, 7L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void deleteClan_shouldPassRequesterUserIdAndClanIdCorrectly() {
        ResponseEntity<ApiMessageResponse> response = clanController.deleteClan(100L, 200L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(clanService).deleteClan(200L, 100L);
        verifyNoMoreInteractions(clanService);
    }

    @Test
    void deleteClan_shouldThrowException_whenServiceFails() {
        doThrow(new RuntimeException("Only leader can delete clan"))
                .when(clanService).deleteClan(99L, 7L);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> clanController.deleteClan(7L, 99L)
        );

        assertEquals("Only leader can delete clan", ex.getMessage());
        verify(clanService).deleteClan(99L, 7L);
        verifyNoMoreInteractions(clanService);
    }
}