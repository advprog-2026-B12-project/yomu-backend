package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanMemberResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanNameAlreadyTakenException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanNameBlankException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UnauthorizedClanActionException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UserAlreadyInClanException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UserNotInClanException;
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
class ClanManagementServiceImplTest {

    private static final UUID LEADER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID OTHER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @Mock private ClanRepository clanRepository;
    @Mock private ClanMemberRepository clanMemberRepository;
    @Mock private ClanJoinRequestRepository clanJoinRequestRepository;

    private ClanManagementService service;
    private Clan clan;

    @BeforeEach
    void setUp() {
        service = new ClanManagementServiceImpl(
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
    void createClan_shouldReturnClanResponseAndSaveLeader_whenValid() {
        when(clanRepository.existsByName("Warriors")).thenReturn(false);
        when(clanMemberRepository.existsByUserId(LEADER_ID)).thenReturn(false);
        when(clanRepository.save(any(Clan.class))).thenReturn(clan);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(i -> i.getArgument(0));
        when(clanMemberRepository.countByClan(clan)).thenReturn(1L);

        ClanResponse result = service.createClan(LEADER_ID, "Warriors", "Fight together");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Warriors", result.getName());
        assertEquals("Fight together", result.getDescription());
        assertEquals(LEADER_ID, result.getLeaderUserId());
        assertEquals("BRONZE", result.getDivision());
        assertEquals(1L, result.getMemberCount());

        ArgumentCaptor<Clan> clanCaptor = ArgumentCaptor.forClass(Clan.class);
        verify(clanRepository).save(clanCaptor.capture());
        assertEquals("Warriors", clanCaptor.getValue().getName());
        assertEquals(LEADER_ID, clanCaptor.getValue().getLeaderUserId());

        ArgumentCaptor<ClanMember> memberCaptor = ArgumentCaptor.forClass(ClanMember.class);
        verify(clanMemberRepository).save(memberCaptor.capture());
        assertEquals(LEADER_ID, memberCaptor.getValue().getUserId());
        assertEquals(ClanMember.Role.LEADER, memberCaptor.getValue().getRole());
    }

    @Test
    void createClan_shouldAllowNullDescription() {
        when(clanRepository.existsByName("Warriors")).thenReturn(false);
        when(clanMemberRepository.existsByUserId(LEADER_ID)).thenReturn(false);
        when(clanRepository.save(any(Clan.class))).thenReturn(clan);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(i -> i.getArgument(0));
        when(clanMemberRepository.countByClan(clan)).thenReturn(1L);

        ClanResponse result = service.createClan(LEADER_ID, "Warriors", null);

        assertNotNull(result);
    }

    @Test
    void createClan_shouldThrowClanNameBlankException_whenNameIsNull() {
        assertThrows(ClanNameBlankException.class,
                () -> service.createClan(LEADER_ID, null, "desc"));
        verifyNoInteractions(clanRepository, clanMemberRepository);
    }

    @Test
    void createClan_shouldThrowClanNameBlankException_whenNameIsBlank() {
        ClanNameBlankException ex = assertThrows(ClanNameBlankException.class,
                () -> service.createClan(LEADER_ID, "   ", "desc"));
        assertEquals("Clan name must not be blank", ex.getMessage());
        verifyNoInteractions(clanRepository, clanMemberRepository);
    }

    @Test
    void createClan_shouldThrowClanNameAlreadyTakenException_whenNameAlreadyExists() {
        when(clanRepository.existsByName("Warriors")).thenReturn(true);

        ClanNameAlreadyTakenException ex = assertThrows(ClanNameAlreadyTakenException.class,
                () -> service.createClan(LEADER_ID, "Warriors", "desc"));

        assertTrue(ex.getMessage().contains("Warriors"));
        verify(clanRepository, never()).save(any(Clan.class));
    }

    @Test
    void createClan_shouldThrowUserAlreadyInClanException_whenUserAlreadyHasClan() {
        when(clanRepository.existsByName("Warriors")).thenReturn(false);
        when(clanMemberRepository.existsByUserId(LEADER_ID)).thenReturn(true);

        UserAlreadyInClanException ex = assertThrows(UserAlreadyInClanException.class,
                () -> service.createClan(LEADER_ID, "Warriors", "desc"));

        assertEquals("User is already in a clan", ex.getMessage());
        verify(clanRepository, never()).save(any(Clan.class));
    }

    @Test
    void getAllClans_shouldReturnMappedClanResponses() {
        Clan clan2 = new Clan();
        clan2.setId(2L);
        clan2.setName("Rangers");
        clan2.setLeaderUserId(OTHER_ID);
        clan2.setDivision("SILVER");

        when(clanRepository.findAll()).thenReturn(List.of(clan, clan2));
        when(clanMemberRepository.countByClan(clan)).thenReturn(3L);
        when(clanMemberRepository.countByClan(clan2)).thenReturn(5L);

        List<ClanResponse> result = service.getAllClans();

        assertEquals(2, result.size());
        assertEquals(LEADER_ID, result.get(0).getLeaderUserId());
        assertEquals(OTHER_ID, result.get(1).getLeaderUserId());
        assertEquals(3L, result.get(0).getMemberCount());
        assertEquals(5L, result.get(1).getMemberCount());
    }

    @Test
    void getAllClans_shouldReturnEmptyList_whenNoClansExist() {
        when(clanRepository.findAll()).thenReturn(List.of());

        List<ClanResponse> result = service.getAllClans();

        assertTrue(result.isEmpty());
        verify(clanMemberRepository, never()).countByClan(any());
    }

    @Test
    void getClanById_shouldReturnMappedClanResponse_whenClanExists() {
        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));
        when(clanMemberRepository.countByClan(clan)).thenReturn(4L);

        ClanResponse result = service.getClanById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Warriors", result.getName());
        assertEquals(LEADER_ID, result.getLeaderUserId());
        assertEquals(4L, result.getMemberCount());
    }

    @Test
    void getClanById_shouldThrowClanNotFoundException_whenClanDoesNotExist() {
        when(clanRepository.findById(999L)).thenReturn(Optional.empty());

        ClanNotFoundException ex = assertThrows(ClanNotFoundException.class,
                () -> service.getClanById(999L));

        assertEquals("Clan not found", ex.getMessage());
        verify(clanMemberRepository, never()).countByClan(any());
    }

    @Test
    void deleteClan_shouldDeleteMembersAndClan_whenRequesterIsLeader() {
        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));

        assertDoesNotThrow(() -> service.deleteClan(LEADER_ID, 1L));

        verify(clanMemberRepository).deleteByClan(clan);
        verify(clanJoinRequestRepository).deleteByClan(clan);
        verify(clanRepository).delete(clan);
    }

    @Test
    void deleteClan_shouldThrowClanNotFoundException_whenClanDoesNotExist() {
        when(clanRepository.findById(999L)).thenReturn(Optional.empty());

        ClanNotFoundException ex = assertThrows(ClanNotFoundException.class,
                () -> service.deleteClan(LEADER_ID, 999L));

        assertEquals("Clan not found", ex.getMessage());
        verify(clanMemberRepository, never()).deleteByClan(any());
    }

    @Test
    void deleteClan_shouldThrowUnauthorizedClanActionException_whenRequesterIsNotLeader() {
        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));

        UnauthorizedClanActionException ex = assertThrows(UnauthorizedClanActionException.class,
                () -> service.deleteClan(OTHER_ID, 1L));

        assertEquals("Only the clan leader can delete the clan", ex.getMessage());
        verify(clanRepository, never()).delete(any());
    }

    @Test
    void getMembers_shouldReturnMappedMemberResponses_whenClanExists() {
        ClanMember leader = member(LEADER_ID, ClanMember.Role.LEADER);
        ClanMember regular = member(MEMBER_ID, ClanMember.Role.MEMBER);

        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));
        when(clanMemberRepository.findByClan(clan)).thenReturn(List.of(leader, regular));

        List<ClanMemberResponse> result = service.getMembers(1L);

        assertEquals(2, result.size());
        assertEquals(LEADER_ID, result.get(0).getUserId());
        assertEquals(MEMBER_ID, result.get(1).getUserId());
    }

    @Test
    void getMembers_shouldReturnEmptyList_whenClanHasNoMembers() {
        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));
        when(clanMemberRepository.findByClan(clan)).thenReturn(List.of());

        List<ClanMemberResponse> result = service.getMembers(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getMembers_shouldThrowClanNotFoundException_whenClanDoesNotExist() {
        when(clanRepository.findById(999L)).thenReturn(Optional.empty());

        ClanNotFoundException ex = assertThrows(ClanNotFoundException.class,
                () -> service.getMembers(999L));

        assertEquals("Clan not found", ex.getMessage());
        verify(clanMemberRepository, never()).findByClan(any());
    }

    @Test
    void getMyClan_shouldReturnClanResponse_whenUserIsInClan() {
        ClanMember membership = new ClanMember();
        membership.setClan(clan);
        membership.setUserId(LEADER_ID);
        membership.setRole(ClanMember.Role.LEADER);

        when(clanMemberRepository.findByUserId(LEADER_ID)).thenReturn(Optional.of(membership));
        when(clanMemberRepository.countByClan(clan)).thenReturn(1L);

        ClanResponse result = service.getMyClan(LEADER_ID);

        assertNotNull(result);
        assertEquals(clan.getId(), result.getId());
    }

    @Test
    void getMyClan_shouldThrowUserNotInClanException_whenUserHasNoClan() {
        when(clanMemberRepository.findByUserId(OTHER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotInClanException.class, () -> service.getMyClan(OTHER_ID));
    }

    private ClanMember member(UUID userId, ClanMember.Role role) {
        ClanMember m = new ClanMember();
        m.setClan(clan);
        m.setUserId(userId);
        m.setRole(role);
        return m;
    }
}
