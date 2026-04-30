package id.ac.ui.cs.advprog.yomubackend.clan.repository;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanMemberRepositoryTest {

    @Mock
    private ClanMemberRepository clanMemberRepository;

    @Test
    void save_returnsSavedMember() {
        Clan clan = buildClan(1L, "Warriors");
        ClanMember member = buildMember(null, clan, 10L, ClanMember.Role.MEMBER);
        ClanMember saved = buildMember(1L, clan, 10L, ClanMember.Role.MEMBER);

        when(clanMemberRepository.save(member)).thenReturn(saved);

        ClanMember result = clanMemberRepository.save(member);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(10L, result.getUserId());
        assertEquals(ClanMember.Role.MEMBER, result.getRole());
        assertEquals(clan, result.getClan());

        verify(clanMemberRepository).save(member);
    }

    @Test
    void findById_returnsPresent_whenMemberExists() {
        Clan clan = buildClan(1L, "Warriors");
        ClanMember member = buildMember(1L, clan, 10L, ClanMember.Role.MEMBER);

        when(clanMemberRepository.findById(1L)).thenReturn(Optional.of(member));

        Optional<ClanMember> result = clanMemberRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getUserId());
        assertEquals(ClanMember.Role.MEMBER, result.get().getRole());

        verify(clanMemberRepository).findById(1L);
    }

    @Test
    void findById_returnsEmpty_whenMemberDoesNotExist() {
        when(clanMemberRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<ClanMember> result = clanMemberRepository.findById(999L);

        assertTrue(result.isEmpty());
        verify(clanMemberRepository).findById(999L);
    }

    @Test
    void delete_callsRepositoryDelete() {
        Clan clan = buildClan(1L, "Warriors");
        ClanMember member = buildMember(1L, clan, 10L, ClanMember.Role.MEMBER);

        clanMemberRepository.delete(member);

        verify(clanMemberRepository, times(1)).delete(member);
    }

    @Test
    void existsByUserId_returnsTrue_whenUserIsMember() {
        when(clanMemberRepository.existsByUserId(10L)).thenReturn(true);

        boolean result = clanMemberRepository.existsByUserId(10L);

        assertTrue(result);
        verify(clanMemberRepository).existsByUserId(10L);
    }

    @Test
    void existsByUserId_returnsFalse_whenUserIsNotMember() {
        when(clanMemberRepository.existsByUserId(999L)).thenReturn(false);

        boolean result = clanMemberRepository.existsByUserId(999L);

        assertFalse(result);
        verify(clanMemberRepository).existsByUserId(999L);
    }

    @Test
    void existsByUserId_passesUserIdCorrectly() {
        when(clanMemberRepository.existsByUserId(anyLong())).thenReturn(true);

        clanMemberRepository.existsByUserId(42L);

        verify(clanMemberRepository).existsByUserId(42L);
    }

    @Test
    void findByUserId_returnsPresent_whenUserExistsInClan() {
        Clan clan = buildClan(1L, "Warriors");
        ClanMember member = buildMember(2L, clan, 42L, ClanMember.Role.LEADER);

        when(clanMemberRepository.findByUserId(42L)).thenReturn(Optional.of(member));

        Optional<ClanMember> result = clanMemberRepository.findByUserId(42L);

        assertTrue(result.isPresent());
        assertEquals(42L, result.get().getUserId());
        assertEquals(ClanMember.Role.LEADER, result.get().getRole());

        verify(clanMemberRepository).findByUserId(42L);
    }

    @Test
    void findByUserId_returnsEmpty_whenUserNotInAnyClan() {
        when(clanMemberRepository.findByUserId(404L)).thenReturn(Optional.empty());

        Optional<ClanMember> result = clanMemberRepository.findByUserId(404L);

        assertTrue(result.isEmpty());
        verify(clanMemberRepository).findByUserId(404L);
    }

    @Test
    void findByClan_returnsMembersOfThatClan() {
        Clan clan = buildClan(1L, "Warriors");
        ClanMember member1 = buildMember(1L, clan, 10L, ClanMember.Role.LEADER);
        ClanMember member2 = buildMember(2L, clan, 20L, ClanMember.Role.MEMBER);

        when(clanMemberRepository.findByClan(clan)).thenReturn(List.of(member1, member2));

        List<ClanMember> result = clanMemberRepository.findByClan(clan);

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getUserId());
        assertEquals(20L, result.get(1).getUserId());

        verify(clanMemberRepository).findByClan(clan);
    }

    @Test
    void findByClan_returnsEmptyList_whenClanHasNoMembers() {
        Clan clan = buildClan(2L, "EmptyClan");

        when(clanMemberRepository.findByClan(clan)).thenReturn(List.of());

        List<ClanMember> result = clanMemberRepository.findByClan(clan);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(clanMemberRepository).findByClan(clan);
    }

    @Test
    void countByClan_returnsMemberCount() {
        Clan clan = buildClan(1L, "Warriors");

        when(clanMemberRepository.countByClan(clan)).thenReturn(3L);

        long result = clanMemberRepository.countByClan(clan);

        assertEquals(3L, result);
        verify(clanMemberRepository).countByClan(clan);
    }

    @Test
    void countByClan_returnsZero_whenClanHasNoMembers() {
        Clan clan = buildClan(2L, "EmptyClan");

        when(clanMemberRepository.countByClan(clan)).thenReturn(0L);

        long result = clanMemberRepository.countByClan(clan);

        assertEquals(0L, result);
        verify(clanMemberRepository).countByClan(clan);
    }

    @Test
    void deleteByClan_callsRepositoryDeleteByClan() {
        Clan clan = buildClan(1L, "Warriors");

        clanMemberRepository.deleteByClan(clan);

        verify(clanMemberRepository).deleteByClan(clan);
    }

    private Clan buildClan(Long id, String name) {
        Clan clan = new Clan();
        clan.setId(id);
        clan.setName(name);
        clan.setLeaderUserId(1L);
        return clan;
    }

    private ClanMember buildMember(Long id, Clan clan, Long userId, ClanMember.Role role) {
        ClanMember member = new ClanMember();
        member.setId(id);
        member.setClan(clan);
        member.setUserId(userId);
        member.setRole(role);
        return member;
    }
}