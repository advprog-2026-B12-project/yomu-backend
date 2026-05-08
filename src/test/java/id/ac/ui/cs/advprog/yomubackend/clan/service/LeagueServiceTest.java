package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.league.ClanScoreProvider;
import id.ac.ui.cs.advprog.yomubackend.clan.league.ClanScoreProviderResolver;
import id.ac.ui.cs.advprog.yomubackend.clan.league.MemberStat;
import id.ac.ui.cs.advprog.yomubackend.clan.league.MemberStatProvider;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeagueServiceTest {

    @Mock
    private ClanRepository clanRepository;

    @Mock
    private ClanMemberRepository clanMemberRepository;

    @Mock
    private MemberStatProvider memberStatProvider;

    @Mock
    private ClanScoreProviderResolver resolver;

    @Mock
    private ClanScoreProvider bronzeProvider;

    @Mock
    private ClanScoreProvider silverProvider;

    @Mock
    private ClanScoreProvider goldProvider;

    @Mock
    private ClanScoreProvider diamondProvider;

    @Mock
    private ClanScoreModifierService clanScoreModifierService;

    private LeagueService leagueService;

    @BeforeEach
    void setUp() {
        leagueService = new LeagueService(
                clanRepository,
                clanMemberRepository,
                memberStatProvider,
                resolver,
                clanScoreModifierService
        );
        lenient().when(clanScoreModifierService.calculateMultiplier(anyList())).thenReturn(1.0);
    }

    @Test
    void getLeaderboardByDivision_shouldReturnRankedLeaderboard_whenDivisionHasClans() {
        Clan clan1 = clan(1L, "Alpha", "BRONZE");
        Clan clan2 = clan(2L, "Beta", "BRONZE");

        ClanMember alphaMember1 = member(10L, clan1);
        ClanMember alphaMember2 = member(11L, clan1);
        ClanMember betaMember1 = member(20L, clan2);

        when(clanRepository.findByDivision("BRONZE")).thenReturn(List.of(clan1, clan2));
        when(clanMemberRepository.findByClan(clan1)).thenReturn(List.of(alphaMember1, alphaMember2));
        when(clanMemberRepository.findByClan(clan2)).thenReturn(List.of(betaMember1));
        when(resolver.resolve("BRONZE")).thenReturn(bronzeProvider);
        when(bronzeProvider.calculateScore(anyList())).thenAnswer(sumTotalScore());

        stubStat(10L, 4);
        stubStat(11L, 6);
        stubStat(20L, 26);

        List<LeaderboardEntryResponse> result = leagueService.getLeaderboardByDivision("bronze");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getRank());
        assertEquals(2L, result.get(0).getClanId());
        assertEquals("Beta", result.get(0).getClanName());
        assertEquals(26, result.get(0).getScore());
        assertEquals(2, result.get(1).getRank());
        assertEquals(1L, result.get(1).getClanId());
        assertEquals("Alpha", result.get(1).getClanName());
        assertEquals(10, result.get(1).getScore());

        verify(clanRepository).findByDivision("BRONZE");
        verify(clanMemberRepository).findByClan(clan1);
        verify(clanMemberRepository).findByClan(clan2);
        verify(resolver, times(2)).resolve("BRONZE");
        verify(bronzeProvider, times(2)).calculateScore(anyList());
    }

    @Test
    void getLeaderboardByDivision_shouldReturnEmptyList_whenNoClansInDivision() {
        when(clanRepository.findByDivision("SILVER")).thenReturn(List.of());

        List<LeaderboardEntryResponse> result = leagueService.getLeaderboardByDivision("silver");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(clanRepository).findByDivision("SILVER");
        verifyNoInteractions(clanMemberRepository, resolver, bronzeProvider, silverProvider, goldProvider, diamondProvider);
    }

    @Test
    void getLeaderboardByDivision_shouldReturnZeroScore_whenClanHasNoMembers() {
        Clan emptyClan = clan(3L, "Lonely Clan", "GOLD");

        when(clanRepository.findByDivision("GOLD")).thenReturn(List.of(emptyClan));
        when(clanMemberRepository.findByClan(emptyClan)).thenReturn(List.of());
        when(resolver.resolve("GOLD")).thenReturn(goldProvider);
        when(goldProvider.calculateScore(anyList())).thenAnswer(sumTotalScore());

        List<LeaderboardEntryResponse> result = leagueService.getLeaderboardByDivision("gold");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getRank());
        assertEquals(3L, result.get(0).getClanId());
        assertEquals("Lonely Clan", result.get(0).getClanName());
        assertEquals("GOLD", result.get(0).getDivision());
        assertEquals(0L, result.get(0).getMemberCount());
        assertEquals(0, result.get(0).getScore());
    }

    @Test
    void getLeaderboardByDivision_shouldApplyScoreMultiplier() {
        Clan clan = clan(1L, "Boosted Clan", "BRONZE");
        ClanMember member = member(10L, clan);

        when(clanRepository.findByDivision("BRONZE")).thenReturn(List.of(clan));
        when(clanMemberRepository.findByClan(clan)).thenReturn(List.of(member));
        when(resolver.resolve("BRONZE")).thenReturn(bronzeProvider);
        when(bronzeProvider.calculateScore(anyList())).thenAnswer(sumTotalScore());
        when(clanScoreModifierService.calculateMultiplier(List.of(member))).thenReturn(1.2);
        stubStat(10L, 10);

        List<LeaderboardEntryResponse> result = leagueService.getLeaderboardByDivision("bronze");

        assertEquals(1, result.size());
        assertEquals(12, result.get(0).getScore());
    }

    @Test
    void getLeaderboardByDivision_shouldThrowNullPointerException_whenDivisionIsNull() {
        assertThrows(NullPointerException.class, () -> leagueService.getLeaderboardByDivision(null));
        verifyNoInteractions(clanRepository, clanMemberRepository, resolver);
    }

    @Test
    void getLeaderboardByDivision_shouldPropagateException_whenResolverFails() {
        Clan clan = clan(1L, "Broken Clan", "BRONZE");
        ClanMember member = member(10L, clan);

        when(clanRepository.findByDivision("BRONZE")).thenReturn(List.of(clan));
        when(clanMemberRepository.findByClan(clan)).thenReturn(List.of(member));
        stubStat(10L, 1);
        when(resolver.resolve("BRONZE")).thenThrow(new IllegalArgumentException("Unknown division provider"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> leagueService.getLeaderboardByDivision("bronze")
        );

        assertEquals("Unknown division provider", ex.getMessage());
        verify(resolver).resolve("BRONZE");
    }

    @Test
    void getLeaderboardByDivision_shouldPropagateException_whenScoreCalculationFails() {
        Clan clan = clan(1L, "Broken Score Clan", "SILVER");
        ClanMember member = member(10L, clan);

        when(clanRepository.findByDivision("SILVER")).thenReturn(List.of(clan));
        when(clanMemberRepository.findByClan(clan)).thenReturn(List.of(member));
        stubStat(10L, 1);
        when(resolver.resolve("SILVER")).thenReturn(silverProvider);
        when(silverProvider.calculateScore(anyList())).thenThrow(new RuntimeException("Score calculation failed"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> leagueService.getLeaderboardByDivision("silver")
        );

        assertEquals("Score calculation failed", ex.getMessage());
        verify(silverProvider).calculateScore(anyList());
    }

    @Test
    void getLeaderboardByDivision_shouldPropagateException_whenMemberStatProviderFails() {
        Clan clan = clan(1L, "Stat Error Clan", "DIAMOND");
        ClanMember member = member(99L, clan);

        when(clanRepository.findByDivision("DIAMOND")).thenReturn(List.of(clan));
        when(clanMemberRepository.findByClan(clan)).thenReturn(List.of(member));
        when(memberStatProvider.getStatForUser(userId(99L))).thenThrow(new RuntimeException("Stat lookup failed"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> leagueService.getLeaderboardByDivision("diamond")
        );

        assertEquals("Stat lookup failed", ex.getMessage());
        verify(memberStatProvider).getStatForUser(userId(99L));
        verifyNoInteractions(resolver, bronzeProvider, silverProvider, goldProvider, diamondProvider);
    }

    @Test
    void triggerSeasonReset_shouldPromoteAndRelegateCorrectlyAcrossDivisions() {
        Clan bronzeTop = clan(1L, "Bronze Top", "BRONZE");
        Clan bronzeBottom = clan(2L, "Bronze Bottom", "BRONZE");
        Clan silverTop = clan(3L, "Silver Top", "SILVER");
        Clan silverBottom = clan(4L, "Silver Bottom", "SILVER");
        Clan goldTop = clan(5L, "Gold Top", "GOLD");
        Clan goldBottom = clan(6L, "Gold Bottom", "GOLD");
        Clan diamondTop = clan(7L, "Diamond Top", "DIAMOND");
        Clan diamondBottom = clan(8L, "Diamond Bottom", "DIAMOND");

        when(clanRepository.findByDivision("BRONZE")).thenReturn(List.of(bronzeTop, bronzeBottom));
        when(clanRepository.findByDivision("SILVER")).thenReturn(List.of(silverTop, silverBottom));
        when(clanRepository.findByDivision("GOLD")).thenReturn(List.of(goldTop, goldBottom));
        when(clanRepository.findByDivision("DIAMOND")).thenReturn(List.of(diamondTop, diamondBottom));
        when(clanMemberRepository.findByClan(any())).thenAnswer(invocation -> {
            Clan clan = invocation.getArgument(0);
            return List.of(member(clan.getId(), clan));
        });
        stubAllProviders();
        stubStat(1L, 100);
        stubStat(2L, 10);
        stubStat(3L, 90);
        stubStat(4L, 20);
        stubStat(5L, 80);
        stubStat(6L, 30);
        stubStat(7L, 70);
        stubStat(8L, 40);

        leagueService.triggerSeasonReset();

        assertEquals("SILVER", bronzeTop.getDivision());
        assertEquals("BRONZE", bronzeBottom.getDivision());
        assertEquals("GOLD", silverTop.getDivision());
        assertEquals("BRONZE", silverBottom.getDivision());
        assertEquals("DIAMOND", goldTop.getDivision());
        assertEquals("SILVER", goldBottom.getDivision());
        assertEquals("DIAMOND", diamondTop.getDivision());
        assertEquals("GOLD", diamondBottom.getDivision());
        verify(clanRepository, times(1)).saveAll(anyList());
    }

    @Test
    void triggerSeasonReset_shouldUseSnapshotAndAvoidDoublePromotion() {
        Clan bronzeTop = clan(1L, "Bronze Top", "BRONZE");
        Clan bronzeBottom = clan(2L, "Bronze Bottom", "BRONZE");
        Clan silverBottom = clan(3L, "Silver Bottom", "SILVER");
        Clan silverTop = clan(4L, "Silver Top", "SILVER");

        when(clanRepository.findByDivision("BRONZE")).thenReturn(List.of(bronzeTop, bronzeBottom));
        when(clanRepository.findByDivision("SILVER")).thenReturn(List.of(silverTop, silverBottom));
        when(clanRepository.findByDivision("GOLD")).thenReturn(List.of());
        when(clanRepository.findByDivision("DIAMOND")).thenReturn(List.of());
        when(clanMemberRepository.findByClan(any())).thenAnswer(invocation -> {
            Clan clan = invocation.getArgument(0);
            return List.of(member(clan.getId(), clan));
        });
        when(resolver.resolve("BRONZE")).thenReturn(bronzeProvider);
        when(resolver.resolve("SILVER")).thenReturn(silverProvider);
        when(bronzeProvider.calculateScore(anyList())).thenAnswer(sumTotalScore());
        when(silverProvider.calculateScore(anyList())).thenAnswer(sumTotalScore());
        stubStat(1L, 100);
        stubStat(2L, 10);
        stubStat(3L, 5);
        stubStat(4L, 90);

        leagueService.triggerSeasonReset();

        assertEquals("SILVER", bronzeTop.getDivision());
        assertEquals("BRONZE", bronzeBottom.getDivision());
        assertEquals("GOLD", silverTop.getDivision());
        assertEquals("BRONZE", silverBottom.getDivision());
        verify(clanRepository, times(1)).saveAll(anyList());
    }

    @Test
    void triggerSeasonReset_shouldCapMovedClansAtFive() {
        List<Clan> bronzeClans = new ArrayList<>();
        for (long i = 1; i <= 12; i++) {
            bronzeClans.add(clan(i, "Bronze " + i, "BRONZE"));
            stubStat(i, (int) (121 - i));
        }

        when(clanRepository.findByDivision("BRONZE")).thenReturn(bronzeClans);
        when(clanRepository.findByDivision("SILVER")).thenReturn(List.of());
        when(clanRepository.findByDivision("GOLD")).thenReturn(List.of());
        when(clanRepository.findByDivision("DIAMOND")).thenReturn(List.of());
        when(clanMemberRepository.findByClan(any())).thenAnswer(invocation -> {
            Clan clan = invocation.getArgument(0);
            return List.of(member(clan.getId(), clan));
        });
        when(resolver.resolve("BRONZE")).thenReturn(bronzeProvider);
        when(bronzeProvider.calculateScore(anyList())).thenAnswer(sumTotalScore());

        leagueService.triggerSeasonReset();

        for (int i = 0; i < 5; i++) {
            assertEquals("SILVER", bronzeClans.get(i).getDivision());
        }
        for (int i = 5; i < 12; i++) {
            assertEquals("BRONZE", bronzeClans.get(i).getDivision());
        }
        verify(clanRepository, times(1)).saveAll(anyList());
    }

    @Test
    void triggerSeasonReset_shouldPropagateException_whenResolverFailsDuringReset() {
        Clan bronzeClanA = clan(1L, "Bronze A", "BRONZE");
        Clan bronzeClanB = clan(2L, "Bronze B", "BRONZE");

        when(clanRepository.findByDivision("BRONZE")).thenReturn(List.of(bronzeClanA, bronzeClanB));
        when(clanRepository.findByDivision("SILVER")).thenReturn(List.of());
        when(clanRepository.findByDivision("GOLD")).thenReturn(List.of());
        when(clanRepository.findByDivision("DIAMOND")).thenReturn(List.of());
        when(clanMemberRepository.findByClan(any())).thenAnswer(invocation -> {
            Clan clan = invocation.getArgument(0);
            return List.of(member(clan.getId(), clan));
        });
        when(memberStatProvider.getStatForUser(any(UUID.class)))
                .thenReturn(new MemberStat(userId(1L), 10, 1, 1.0));
        when(resolver.resolve("BRONZE")).thenThrow(new IllegalArgumentException("No provider for BRONZE"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> leagueService.triggerSeasonReset()
        );

        assertEquals("No provider for BRONZE", ex.getMessage());
        verify(clanRepository, never()).saveAll(anyList());
    }

    private void stubAllProviders() {
        when(resolver.resolve("BRONZE")).thenReturn(bronzeProvider);
        when(resolver.resolve("SILVER")).thenReturn(silverProvider);
        when(resolver.resolve("GOLD")).thenReturn(goldProvider);
        when(resolver.resolve("DIAMOND")).thenReturn(diamondProvider);
        when(bronzeProvider.calculateScore(anyList())).thenAnswer(sumTotalScore());
        when(silverProvider.calculateScore(anyList())).thenAnswer(sumTotalScore());
        when(goldProvider.calculateScore(anyList())).thenAnswer(sumTotalScore());
        when(diamondProvider.calculateScore(anyList())).thenAnswer(sumTotalScore());
    }

    private void stubStat(long userId, int totalScore) {
        when(memberStatProvider.getStatForUser(userId(userId)))
                .thenReturn(new MemberStat(userId(userId), totalScore, 1, 1.0));
    }

    private Answer<Double> sumTotalScore() {
        return invocation -> {
            List<MemberStat> stats = invocation.getArgument(0);
            return (double) stats.stream()
                    .mapToInt(MemberStat::totalScore)
                    .sum();
        };
    }

    private Clan clan(Long id, String name, String division) {
        Clan clan = new Clan();
        clan.setId(id);
        clan.setName(name);
        clan.setDivision(division);
        return clan;
    }

    private ClanMember member(Long userId, Clan clan) {
        ClanMember member = new ClanMember();
        member.setUserId(userId(userId));
        member.setClan(clan);
        member.setRole(ClanMember.Role.MEMBER);
        return member;
    }

    private UUID userId(long value) {
        return new UUID(0L, value);
    }
}
