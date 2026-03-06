package id.ac.ui.cs.advprog.yomubackend.repository;

import id.ac.ui.cs.advprog.yomubackend.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.entity.ClanMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanMemberRepositoryTest {

    @Mock
    private ClanMemberRepository clanMemberRepository;

    // ────────────────────────────────────────────────
    // save
    // ────────────────────────────────────────────────

    @Test
    void save_returnsSavedMember() {
        Clan clan = buildClan(1L, "Warriors");
        ClanMember member = buildMember(null, clan, 10L, ClanMember.Role.MEMBER);
        ClanMember saved  = buildMember(1L,  clan, 10L, ClanMember.Role.MEMBER);
        when(clanMemberRepository.save(member)).thenReturn(saved);

        ClanMember result = clanMemberRepository.save(member);

        assertNotNull(result.getId());
        assertEquals(10L, result.getUserId());
        assertEquals(ClanMember.Role.MEMBER, result.getRole());
        verify(clanMemberRepository).save(member);
    }

    // ────────────────────────────────────────────────
    // findById
    // ────────────────────────────────────────────────

    @Test
    void findById_returnsPresent_whenMemberExists() {
        Clan clan = buildClan(1L, "Warriors");
        ClanMember member = buildMember(1L, clan, 10L, ClanMember.Role.MEMBER);
        when(clanMemberRepository.findById(1L)).thenReturn(Optional.of(member));

        Optional<ClanMember> result = clanMemberRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getUserId());
    }

    @Test
    void findById_returnsEmpty_whenNotExists() {
        when(clanMemberRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<ClanMember> result = clanMemberRepository.findById(999L);

        assertTrue(result.isEmpty());
    }

    // ────────────────────────────────────────────────
    // delete
    // ────────────────────────────────────────────────

    @Test
    void delete_callsRepositoryDelete() {
        Clan clan = buildClan(1L, "Warriors");
        ClanMember member = buildMember(1L, clan, 10L, ClanMember.Role.MEMBER);

        clanMemberRepository.delete(member);

        verify(clanMemberRepository, times(1)).delete(member);
    }

    // ────────────────────────────────────────────────
    // existsByUserId
    // ────────────────────────────────────────────────

    @Test
    void existsByUserId_returnsTrue_whenUserIsMember() {
        when(clanMemberRepository.existsByUserId(10L)).thenReturn(true);

        assertTrue(clanMemberRepository.existsByUserId(10L));
    }

    @Test
    void existsByUserId_returnsFalse_whenUserNotMember() {
        when(clanMemberRepository.existsByUserId(999L)).thenReturn(false);

        assertFalse(clanMemberRepository.existsByUserId(999L));
    }

    @Test
    void existsByUserId_returnsFalse_whenRepoEmpty() {
        when(clanMemberRepository.existsByUserId(anyLong())).thenReturn(false);

        assertFalse(clanMemberRepository.existsByUserId(10L));
    }

    @Test
    void existsByUserId_passesUserIdCorrectlyToRepository() {
        when(clanMemberRepository.existsByUserId(anyLong())).thenReturn(true);

        clanMemberRepository.existsByUserId(42L);

        verify(clanMemberRepository).existsByUserId(42L);
    }

    // ────────────────────────────────────────────────
    // existsByClan_IdAndUserId
    // ────────────────────────────────────────────────

    @Test
    void existsByClanIdAndUserId_returnsTrue_whenBothMatch() {
        when(clanMemberRepository.existsByClan_IdAndUserId(1L, 10L)).thenReturn(true);

        assertTrue(clanMemberRepository.existsByClan_IdAndUserId(1L, 10L));
    }

    @Test
    void existsByClanIdAndUserId_returnsFalse_whenWrongUserId() {
        when(clanMemberRepository.existsByClan_IdAndUserId(1L, 999L)).thenReturn(false);

        assertFalse(clanMemberRepository.existsByClan_IdAndUserId(1L, 999L));
    }

    @Test
    void existsByClanIdAndUserId_returnsFalse_whenWrongClanId() {
        when(clanMemberRepository.existsByClan_IdAndUserId(999L, 10L)).thenReturn(false);

        assertFalse(clanMemberRepository.existsByClan_IdAndUserId(999L, 10L));
    }

    @Test
    void existsByClanIdAndUserId_returnsFalse_whenBothWrong() {
        when(clanMemberRepository.existsByClan_IdAndUserId(999L, 999L)).thenReturn(false);

        assertFalse(clanMemberRepository.existsByClan_IdAndUserId(999L, 999L));
    }

    @Test
    void existsByClanIdAndUserId_distinguishesBetweenClans() {
        when(clanMemberRepository.existsByClan_IdAndUserId(1L, 10L)).thenReturn(true);
        when(clanMemberRepository.existsByClan_IdAndUserId(2L, 10L)).thenReturn(false);

        assertTrue(clanMemberRepository.existsByClan_IdAndUserId(1L, 10L));
        assertFalse(clanMemberRepository.existsByClan_IdAndUserId(2L, 10L));
    }

    @Test
    void existsByClanIdAndUserId_passesBothArgsCorrectly() {
        when(clanMemberRepository.existsByClan_IdAndUserId(anyLong(), anyLong())).thenReturn(true);

        clanMemberRepository.existsByClan_IdAndUserId(5L, 20L);

        verify(clanMemberRepository).existsByClan_IdAndUserId(5L, 20L);
    }

    // ────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────

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