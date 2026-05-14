package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanMemberResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UnauthorizedClanActionException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UserAlreadyInClanException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UserNotInClanException;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanServiceTest {

    @Mock
    private ClanRepository clanRepository;

    @Mock
    private ClanMemberRepository clanMemberRepository;

    @InjectMocks
    private ClanService clanService;

    private Clan clan;

    @BeforeEach
    void setUp() {
        clan = new Clan();
        clan.setId(1L);
        clan.setName("Warriors");
        clan.setDescription("Fight together");
        clan.setLeaderUserId(42L);
        clan.setDivision("BRONZE");
    }

    @Test
    void createClan_shouldReturnClanResponseAndSaveLeader_whenValid() {
        when(clanRepository.existsByName("Warriors")).thenReturn(false);
        when(clanMemberRepository.existsByUserId(42L)).thenReturn(false);
        when(clanRepository.save(any(Clan.class))).thenReturn(clan);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(clanMemberRepository.countByClan(clan)).thenReturn(1L);

        ClanResponse result = clanService.createClan(42L, "Warriors", "Fight together");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Warriors", result.getName());
        assertEquals("Fight together", result.getDescription());
        assertEquals(42L, result.getLeaderUserId());
        assertEquals("BRONZE", result.getDivision());
        assertEquals(1L, result.getMemberCount());

        ArgumentCaptor<Clan> clanCaptor = ArgumentCaptor.forClass(Clan.class);
        verify(clanRepository).save(clanCaptor.capture());
        assertEquals("Warriors", clanCaptor.getValue().getName());
        assertEquals("Fight together", clanCaptor.getValue().getDescription());
        assertEquals(42L, clanCaptor.getValue().getLeaderUserId());
        assertEquals("BRONZE", clanCaptor.getValue().getDivision());

        ArgumentCaptor<ClanMember> memberCaptor = ArgumentCaptor.forClass(ClanMember.class);
        verify(clanMemberRepository).save(memberCaptor.capture());
        assertEquals(clan, memberCaptor.getValue().getClan());
        assertEquals(42L, memberCaptor.getValue().getUserId());
        assertEquals(ClanMember.Role.LEADER, memberCaptor.getValue().getRole());
    }

    @Test
    void createClan_shouldAllowNullDescription() {
        when(clanRepository.existsByName("Warriors")).thenReturn(false);
        when(clanMemberRepository.existsByUserId(42L)).thenReturn(false);
        when(clanRepository.save(any(Clan.class))).thenReturn(clan);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(clanMemberRepository.countByClan(clan)).thenReturn(1L);

        ClanResponse result = clanService.createClan(42L, "Warriors", null);

        assertNotNull(result);

        ArgumentCaptor<Clan> clanCaptor = ArgumentCaptor.forClass(Clan.class);
        verify(clanRepository).save(clanCaptor.capture());
        assertNull(clanCaptor.getValue().getDescription());
    }

    @Test
    void createClan_shouldThrowIllegalArgumentException_whenNameIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clanService.createClan(42L, null, "desc")
        );

        assertEquals("Clan name must not be blank", ex.getMessage());
        verifyNoInteractions(clanRepository, clanMemberRepository);
    }

    @Test
    void createClan_shouldThrowIllegalArgumentException_whenNameIsBlank() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clanService.createClan(42L, "   ", "desc")
        );

        assertEquals("Clan name must not be blank", ex.getMessage());
        verifyNoInteractions(clanRepository, clanMemberRepository);
    }

    @Test
    void createClan_shouldThrowIllegalArgumentException_whenNameAlreadyExists() {
        when(clanRepository.existsByName("Warriors")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clanService.createClan(42L, "Warriors", "desc")
        );

        assertEquals("Clan name already exists", ex.getMessage());
        verify(clanRepository, never()).save(any(Clan.class));
        verify(clanMemberRepository, never()).save(any(ClanMember.class));
    }

    @Test
    void createClan_shouldThrowUserAlreadyInClanException_whenUserAlreadyHasClan() {
        when(clanRepository.existsByName("Warriors")).thenReturn(false);
        when(clanMemberRepository.existsByUserId(42L)).thenReturn(true);

        UserAlreadyInClanException ex = assertThrows(
                UserAlreadyInClanException.class,
                () -> clanService.createClan(42L, "Warriors", "desc")
        );

        assertEquals("User is already in a clan", ex.getMessage());
        verify(clanRepository, never()).save(any(Clan.class));
        verify(clanMemberRepository, never()).save(any(ClanMember.class));
    }

    @Test
    void joinClan_shouldReturnClanMemberResponse_whenValid() {
        ClanMember savedMember = new ClanMember();
        savedMember.setClan(clan);
        savedMember.setUserId(7L);
        savedMember.setRole(ClanMember.Role.MEMBER);

        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));
        when(clanMemberRepository.existsByUserId(7L)).thenReturn(false);
        when(clanMemberRepository.save(any(ClanMember.class))).thenReturn(savedMember);

        ClanMemberResponse result = clanService.joinClan(7L, 1L);

        assertNotNull(result);
        assertEquals(7L, result.getUserId());
        assertEquals(ClanMember.Role.MEMBER, result.getRole());

        ArgumentCaptor<ClanMember> memberCaptor = ArgumentCaptor.forClass(ClanMember.class);
        verify(clanMemberRepository).save(memberCaptor.capture());
        assertEquals(clan, memberCaptor.getValue().getClan());
        assertEquals(7L, memberCaptor.getValue().getUserId());
        assertEquals(ClanMember.Role.MEMBER, memberCaptor.getValue().getRole());
    }

    @Test
    void joinClan_shouldThrowClanNotFoundException_whenClanDoesNotExist() {
        when(clanRepository.findById(999L)).thenReturn(Optional.empty());

        ClanNotFoundException ex = assertThrows(
                ClanNotFoundException.class,
                () -> clanService.joinClan(7L, 999L)
        );

        assertEquals("Clan not found", ex.getMessage());
        verify(clanMemberRepository, never()).save(any(ClanMember.class));
    }

    @Test
    void joinClan_shouldThrowUserAlreadyInClanException_whenUserAlreadyInClan() {
        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));
        when(clanMemberRepository.existsByUserId(7L)).thenReturn(true);

        UserAlreadyInClanException ex = assertThrows(
                UserAlreadyInClanException.class,
                () -> clanService.joinClan(7L, 1L)
        );

        assertEquals("User is already in a clan", ex.getMessage());
        verify(clanMemberRepository, never()).save(any(ClanMember.class));
    }

    @Test
    void leaveClan_shouldDeleteMember_whenUserIsRegularMember() {
        ClanMember member = new ClanMember();
        member.setUserId(7L);
        member.setRole(ClanMember.Role.MEMBER);
        member.setClan(clan);

        when(clanMemberRepository.findByUserId(7L)).thenReturn(Optional.of(member));

        assertDoesNotThrow(() -> clanService.leaveClan(7L));

        verify(clanMemberRepository).delete(member);
    }

    @Test
    void leaveClan_shouldThrowUserNotInClanException_whenUserHasNoClan() {
        when(clanMemberRepository.findByUserId(7L)).thenReturn(Optional.empty());

        UserNotInClanException ex = assertThrows(
                UserNotInClanException.class,
                () -> clanService.leaveClan(7L)
        );

        assertEquals("User is not in any clan", ex.getMessage());
        verify(clanMemberRepository, never()).delete(any());
    }

    @Test
    void leaveClan_shouldThrowIllegalArgumentException_whenUserIsLeader() {
        ClanMember leader = new ClanMember();
        leader.setUserId(42L);
        leader.setRole(ClanMember.Role.LEADER);
        leader.setClan(clan);

        when(clanMemberRepository.findByUserId(42L)).thenReturn(Optional.of(leader));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clanService.leaveClan(42L)
        );

        assertEquals("Clan leader cannot leave the clan. Delete the clan instead.", ex.getMessage());
        verify(clanMemberRepository, never()).delete(any());
    }

    @Test
    void deleteClan_shouldDeleteMembersAndClan_whenRequesterIsLeader() {
        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));

        assertDoesNotThrow(() -> clanService.deleteClan(42L, 1L));

        verify(clanMemberRepository).deleteByClan(clan);
        verify(clanRepository).delete(clan);
    }

    @Test
    void deleteClan_shouldThrowClanNotFoundException_whenClanDoesNotExist() {
        when(clanRepository.findById(999L)).thenReturn(Optional.empty());

        ClanNotFoundException ex = assertThrows(
                ClanNotFoundException.class,
                () -> clanService.deleteClan(42L, 999L)
        );

        assertEquals("Clan not found", ex.getMessage());
        verify(clanMemberRepository, never()).deleteByClan(any());
        verify(clanRepository, never()).delete(any());
    }

    @Test
    void deleteClan_shouldThrowUnauthorizedClanActionException_whenRequesterIsNotLeader() {
        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));

        UnauthorizedClanActionException ex = assertThrows(
                UnauthorizedClanActionException.class,
                () -> clanService.deleteClan(99L, 1L)
        );

        assertEquals("Only the clan leader can delete the clan", ex.getMessage());
        verify(clanMemberRepository, never()).deleteByClan(any());
        verify(clanRepository, never()).delete(any());
    }

    @Test
    void getAllClans_shouldReturnMappedClanResponses() {
        Clan clan2 = new Clan();
        clan2.setId(2L);
        clan2.setName("Rangers");
        clan2.setDescription("Scout ahead");
        clan2.setLeaderUserId(99L);
        clan2.setDivision("SILVER");

        when(clanRepository.findAll()).thenReturn(List.of(clan, clan2));
        when(clanMemberRepository.countByClan(clan)).thenReturn(3L);
        when(clanMemberRepository.countByClan(clan2)).thenReturn(5L);

        List<ClanResponse> result = clanService.getAllClans();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("Warriors", result.get(0).getName());
        assertEquals(42L, result.get(0).getLeaderUserId());
        assertEquals("BRONZE", result.get(0).getDivision());
        assertEquals(3L, result.get(0).getMemberCount());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Rangers", result.get(1).getName());
        assertEquals(99L, result.get(1).getLeaderUserId());
        assertEquals("SILVER", result.get(1).getDivision());
        assertEquals(5L, result.get(1).getMemberCount());
    }

    @Test
    void getAllClans_shouldReturnEmptyList_whenNoClansExist() {
        when(clanRepository.findAll()).thenReturn(List.of());

        List<ClanResponse> result = clanService.getAllClans();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(clanMemberRepository, never()).countByClan(any());
    }

    @Test
    void getClanById_shouldReturnMappedClanResponse_whenClanExists() {
        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));
        when(clanMemberRepository.countByClan(clan)).thenReturn(4L);

        ClanResponse result = clanService.getClanById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Warriors", result.getName());
        assertEquals("Fight together", result.getDescription());
        assertEquals(42L, result.getLeaderUserId());
        assertEquals("BRONZE", result.getDivision());
        assertEquals(4L, result.getMemberCount());
    }

    @Test
    void getClanById_shouldThrowClanNotFoundException_whenClanDoesNotExist() {
        when(clanRepository.findById(999L)).thenReturn(Optional.empty());

        ClanNotFoundException ex = assertThrows(
                ClanNotFoundException.class,
                () -> clanService.getClanById(999L)
        );

        assertEquals("Clan not found", ex.getMessage());
        verify(clanMemberRepository, never()).countByClan(any());
    }

    // =========================================================
    // getMembers
    // =========================================================

    @Test
    void getMembers_shouldReturnMappedMemberResponses_whenClanExists() {
        ClanMember leader = new ClanMember();
        leader.setClan(clan);
        leader.setUserId(42L);
        leader.setRole(ClanMember.Role.LEADER);

        ClanMember member = new ClanMember();
        member.setClan(clan);
        member.setUserId(7L);
        member.setRole(ClanMember.Role.MEMBER);

        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));
        when(clanMemberRepository.findByClan(clan)).thenReturn(List.of(leader, member));

        List<ClanMemberResponse> result = clanService.getMembers(1L);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(42L, result.get(0).getUserId());
        assertEquals(ClanMember.Role.LEADER, result.get(0).getRole());

        assertEquals(7L, result.get(1).getUserId());
        assertEquals(ClanMember.Role.MEMBER, result.get(1).getRole());
    }

    @Test
    void getMembers_shouldReturnEmptyList_whenClanHasNoMembers() {
        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));
        when(clanMemberRepository.findByClan(clan)).thenReturn(List.of());

        List<ClanMemberResponse> result = clanService.getMembers(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getMembers_shouldThrowClanNotFoundException_whenClanDoesNotExist() {
        when(clanRepository.findById(999L)).thenReturn(Optional.empty());

        ClanNotFoundException ex = assertThrows(
                ClanNotFoundException.class,
                () -> clanService.getMembers(999L)
        );

        assertEquals("Clan not found", ex.getMessage());
        verify(clanMemberRepository, never()).findByClan(any());
    }
}