package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanJoinRequestResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanMemberResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanJoinRequest;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanJoinRequestNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.JoinRequestAlreadyResolvedException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.PendingJoinRequestAlreadyExistsException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UnauthorizedClanActionException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UserAlreadyInClanException;
import id.ac.ui.cs.advprog.yomubackend.clan.mapper.ClanMapper;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanJoinRequestRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanJoinRequestServiceImplTest {

    private static final UUID LEADER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID OTHER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @Mock private ClanRepository clanRepository;
    @Mock private ClanMemberRepository clanMemberRepository;
    @Mock private ClanJoinRequestRepository clanJoinRequestRepository;

    private ClanJoinRequestService service;
    private Clan clan;

    @BeforeEach
    void setUp() {
        service = new ClanJoinRequestServiceImpl(
                clanRepository,
                clanMemberRepository,
                clanJoinRequestRepository,
                new ClanMapper()
        );

        clan = new Clan();
        clan.setId(1L);
        clan.setName("Warriors");
        clan.setDescription("Fight together");
        clan.setLeaderUserId(LEADER_ID);
        clan.setDivision("BRONZE");
    }

    @Test
    void requestToJoinClan_shouldReturnPendingJoinRequest_whenValid() {
        ClanJoinRequest saved = joinRequest(10L, clan, MEMBER_ID, ClanJoinRequest.Status.PENDING);

        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));
        when(clanMemberRepository.existsByUserId(MEMBER_ID)).thenReturn(false);
        when(clanJoinRequestRepository.existsByUserIdAndStatus(MEMBER_ID, ClanJoinRequest.Status.PENDING))
                .thenReturn(false);
        when(clanJoinRequestRepository.save(any(ClanJoinRequest.class))).thenReturn(saved);

        ClanJoinRequestResponse result = service.requestToJoinClan(MEMBER_ID, 1L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(1L, result.getClanId());
        assertEquals("Warriors", result.getClanName());
        assertEquals(MEMBER_ID, result.getUserId());
        assertEquals("PENDING", result.getStatus());

        ArgumentCaptor<ClanJoinRequest> captor = ArgumentCaptor.forClass(ClanJoinRequest.class);
        verify(clanJoinRequestRepository).save(captor.capture());
        assertEquals(clan, captor.getValue().getClan());
        assertEquals(ClanJoinRequest.Status.PENDING, captor.getValue().getStatus());
    }

    @Test
    void requestToJoinClan_shouldThrowClanNotFoundException_whenClanDoesNotExist() {
        when(clanRepository.findById(999L)).thenReturn(Optional.empty());

        ClanNotFoundException ex = assertThrows(ClanNotFoundException.class,
                () -> service.requestToJoinClan(MEMBER_ID, 999L));

        assertEquals("Clan not found", ex.getMessage());
        verify(clanJoinRequestRepository, never()).save(any());
    }

    @Test
    void requestToJoinClan_shouldThrowUserAlreadyInClanException_whenUserAlreadyInClan() {
        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));
        when(clanMemberRepository.existsByUserId(MEMBER_ID)).thenReturn(true);

        UserAlreadyInClanException ex = assertThrows(UserAlreadyInClanException.class,
                () -> service.requestToJoinClan(MEMBER_ID, 1L));

        assertEquals("User is already in a clan", ex.getMessage());
        verify(clanJoinRequestRepository, never()).save(any());
    }

    @Test
    void requestToJoinClan_shouldThrowPendingJoinRequestAlreadyExistsException_whenUserAlreadyHasPendingRequest() {
        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));
        when(clanMemberRepository.existsByUserId(MEMBER_ID)).thenReturn(false);
        when(clanJoinRequestRepository.existsByUserIdAndStatus(MEMBER_ID, ClanJoinRequest.Status.PENDING))
                .thenReturn(true);

        PendingJoinRequestAlreadyExistsException ex = assertThrows(
                PendingJoinRequestAlreadyExistsException.class,
                () -> service.requestToJoinClan(MEMBER_ID, 1L));

        assertEquals("User already has a pending clan join request", ex.getMessage());
        verify(clanJoinRequestRepository, never()).save(any());
    }

    @Test
    void getPendingJoinRequests_shouldReturnPendingRequests_whenRequesterIsLeader() {
        ClanJoinRequest request = joinRequest(10L, clan, MEMBER_ID, ClanJoinRequest.Status.PENDING);

        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));
        when(clanJoinRequestRepository.findByClanAndStatus(clan, ClanJoinRequest.Status.PENDING))
                .thenReturn(List.of(request));

        List<ClanJoinRequestResponse> result = service.getPendingJoinRequests(LEADER_ID, 1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(MEMBER_ID, result.get(0).getUserId());
        assertEquals("PENDING", result.get(0).getStatus());
    }

    @Test
    void getPendingJoinRequests_shouldThrowUnauthorizedClanActionException_whenRequesterIsNotLeader() {
        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));

        UnauthorizedClanActionException ex = assertThrows(UnauthorizedClanActionException.class,
                () -> service.getPendingJoinRequests(OTHER_ID, 1L));

        assertEquals("Only the clan leader can manage join requests", ex.getMessage());
        verify(clanJoinRequestRepository, never()).findByClanAndStatus(any(), any());
    }

    @Test
    void approveJoinRequest_shouldCreateMemberAndApproveRequest_whenRequesterIsLeader() {
        ClanJoinRequest request = joinRequest(10L, clan, MEMBER_ID, ClanJoinRequest.Status.PENDING);
        ClanMember savedMember = new ClanMember();
        savedMember.setClan(clan);
        savedMember.setUserId(MEMBER_ID);
        savedMember.setRole(ClanMember.Role.MEMBER);

        when(clanJoinRequestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(clanMemberRepository.existsByUserId(MEMBER_ID)).thenReturn(false);
        when(clanMemberRepository.save(any(ClanMember.class))).thenReturn(savedMember);
        when(clanJoinRequestRepository.save(any(ClanJoinRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(clanJoinRequestRepository.findByUserIdAndStatus(MEMBER_ID, ClanJoinRequest.Status.PENDING))
                .thenReturn(List.of(request));

        ClanMemberResponse result = service.approveJoinRequest(LEADER_ID, 10L);

        assertNotNull(result);
        assertEquals(MEMBER_ID, result.getUserId());
        assertEquals(ClanMember.Role.MEMBER, result.getRole());
        assertEquals(ClanJoinRequest.Status.APPROVED, request.getStatus());
        assertNotNull(request.getResolvedAt());
    }

    @Test
    void approveJoinRequest_shouldThrowClanJoinRequestNotFoundException_whenRequestDoesNotExist() {
        when(clanJoinRequestRepository.findById(404L)).thenReturn(Optional.empty());

        ClanJoinRequestNotFoundException ex = assertThrows(ClanJoinRequestNotFoundException.class,
                () -> service.approveJoinRequest(LEADER_ID, 404L));

        assertEquals("Clan join request not found", ex.getMessage());
        verify(clanMemberRepository, never()).save(any());
    }

    @Test
    void approveJoinRequest_shouldThrowUnauthorizedClanActionException_whenRequesterIsNotLeader() {
        ClanJoinRequest request = joinRequest(10L, clan, MEMBER_ID, ClanJoinRequest.Status.PENDING);
        when(clanJoinRequestRepository.findById(10L)).thenReturn(Optional.of(request));

        UnauthorizedClanActionException ex = assertThrows(UnauthorizedClanActionException.class,
                () -> service.approveJoinRequest(OTHER_ID, 10L));

        assertEquals("Only the clan leader can manage join requests", ex.getMessage());
        verify(clanMemberRepository, never()).save(any());
    }

    @Test
    void approveJoinRequest_shouldThrowJoinRequestAlreadyResolvedException_whenRequestAlreadyResolved() {
        ClanJoinRequest request = joinRequest(10L, clan, MEMBER_ID, ClanJoinRequest.Status.REJECTED);
        when(clanJoinRequestRepository.findById(10L)).thenReturn(Optional.of(request));

        JoinRequestAlreadyResolvedException ex = assertThrows(JoinRequestAlreadyResolvedException.class,
                () -> service.approveJoinRequest(LEADER_ID, 10L));

        assertEquals("Clan join request is already resolved", ex.getMessage());
        verify(clanMemberRepository, never()).save(any());
    }

    @Test
    void rejectJoinRequest_shouldRejectRequest_whenRequesterIsLeader() {
        ClanJoinRequest request = joinRequest(10L, clan, MEMBER_ID, ClanJoinRequest.Status.PENDING);
        when(clanJoinRequestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(clanJoinRequestRepository.save(any(ClanJoinRequest.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> service.rejectJoinRequest(LEADER_ID, 10L));

        assertEquals(ClanJoinRequest.Status.REJECTED, request.getStatus());
        assertNotNull(request.getResolvedAt());
    }

    private ClanJoinRequest joinRequest(Long id, Clan clan, UUID userId, ClanJoinRequest.Status status) {
        ClanJoinRequest r = new ClanJoinRequest();
        r.setId(id);
        r.setClan(clan);
        r.setUserId(userId);
        r.setStatus(status);
        return r;
    }
}
