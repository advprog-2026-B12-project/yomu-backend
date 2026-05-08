package id.ac.ui.cs.advprog.yomubackend.clan.repository;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanMemberRepositoryTest {

    private static final UUID LEADER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID MISSING_ID = UUID.fromString("00000000-0000-0000-0000-000000000404");

    @Mock
    private ClanMemberRepository clanMemberRepository;

    @Test
    void save_returnsSavedMember() {
        Clan clan = buildClan(1L, "Warriors");
        ClanMember member = buildMember(null, clan, MEMBER_ID, ClanMember.Role.MEMBER);
        ClanMember saved = buildMember(1L, clan, MEMBER_ID, ClanMember.Role.MEMBER);

        when(clanMemberRepository.save(member)).thenReturn(saved);

        ClanMember result = clanMemberRepository.save(member);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(MEMBER_ID, result.getUserId());
        assertEquals(ClanMember.Role.MEMBER, result.getRole());
        assertEquals(clan, result.getClan());
        verify(clanMemberRepository).save(member);
    }

    @Test
    void findById_returnsPresent_whenMemberExists() {
        Clan clan = buildClan(1L, "Warriors");
        ClanMember member = buildMember(1L, clan, MEMBER_ID, ClanMember.Role.MEMBER);

        when(clanMemberRepository.findById(1L)).thenReturn(Optional.of(member));

        Optional<ClanMember> result = clanMemberRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(MEMBER_ID, result.get().getUserId());
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
        ClanMember member = buildMember(1L, clan, MEMBER_ID, ClanMember.Role.MEMBER);

        clanMemberRepository.delete(member);

        verify(clanMemberRepository, times(1)).delete(member);
    }

    @Test
    void existsByUserId_returnsTrue_whenUserIsMember() {
        when(clanMemberRepository.existsByUserId(MEMBER_ID)).thenReturn(true);

        boolean result = clanMemberRepository.existsByUserId(MEMBER_ID);

        assertTrue(result);
        verify(clanMemberRepository).existsByUserId(MEMBER_ID);
    }

    @Test
    void existsByUserId_returnsFalse_whenUserIsNotMember() {
        when(clanMemberRepository.existsByUserId(MISSING_ID)).thenReturn(false);

        boolean result = clanMemberRepository.existsByUserId(MISSING_ID);

        assertFalse(result);
        verify(clanMemberRepository).existsByUserId(MISSING_ID);
    }

    @Test
    void existsByUserId_passesUserIdCorrectly() {
        when(clanMemberRepository.existsByUserId(any(UUID.class))).thenReturn(true);

        clanMemberRepository.existsByUserId(MEMBER_ID);

        verify(clanMemberRepository).existsByUserId(MEMBER_ID);
    }

    @Test
    void findByUserId_returnsPresent_whenUserExistsInClan() {
        Clan clan = buildClan(1L, "Warriors");
        ClanMember member = buildMember(2L, clan, LEADER_ID, ClanMember.Role.LEADER);

        when(clanMemberRepository.findByUserId(LEADER_ID)).thenReturn(Optional.of(member));

        Optional<ClanMember> result = clanMemberRepository.findByUserId(LEADER_ID);

        assertTrue(result.isPresent());
        assertEquals(LEADER_ID, result.get().getUserId());
        assertEquals(ClanMember.Role.LEADER, result.get().getRole());
        verify(clanMemberRepository).findByUserId(LEADER_ID);
    }

    @Test
    void findByUserId_returnsEmpty_whenUserNotInAnyClan() {
        when(clanMemberRepository.findByUserId(MISSING_ID)).thenReturn(Optional.empty());

        Optional<ClanMember> result = clanMemberRepository.findByUserId(MISSING_ID);

        assertTrue(result.isEmpty());
        verify(clanMemberRepository).findByUserId(MISSING_ID);
    }

    @Test
    void findByClan_returnsMembersOfThatClan() {
        Clan clan = buildClan(1L, "Warriors");
        ClanMember member1 = buildMember(1L, clan, LEADER_ID, ClanMember.Role.LEADER);
        ClanMember member2 = buildMember(2L, clan, MEMBER_ID, ClanMember.Role.MEMBER);

        when(clanMemberRepository.findByClan(clan)).thenReturn(List.of(member1, member2));

        List<ClanMember> result = clanMemberRepository.findByClan(clan);

        assertEquals(2, result.size());
        assertEquals(LEADER_ID, result.get(0).getUserId());
        assertEquals(MEMBER_ID, result.get(1).getUserId());
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
        clan.setLeaderUserId(LEADER_ID);
        return clan;
    }

    private ClanMember buildMember(Long id, Clan clan, UUID userId, ClanMember.Role role) {
        ClanMember member = new ClanMember();
        member.setId(id);
        member.setClan(clan);
        member.setUserId(userId);
        member.setRole(role);
        return member;
    }
}
