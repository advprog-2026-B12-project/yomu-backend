package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMemberQuizStat;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberDailyMissionCompletionRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberQuizStatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompositeClanScoreMultiplierCalculatorTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-05-07T12:00:00Z"),
            ZoneOffset.UTC
    );
    private static final LocalDate TODAY = LocalDate.of(2026, 5, 7);
    private static final LocalDateTime CURRENT_WINDOW_START = LocalDateTime.of(2026, 4, 30, 12, 0);

    @Mock
    private ClanMemberDailyMissionCompletionRepository completionRepository;

    @Mock
    private ClanMemberQuizStatRepository quizStatRepository;

    private CompositeClanScoreMultiplierCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new CompositeClanScoreMultiplierCalculator(
                completionRepository,
                quizStatRepository,
                FIXED_CLOCK
        );
    }

    @Test
    void calculateMultiplier_shouldReturnNeutralMultiplier_whenClanHasNoMembers() {
        double result = calculator.calculateMultiplier(List.of());

        assertEquals(1.0, result);
    }

    @Test
    void calculateMultiplier_shouldReturnNeutralMultiplier_whenMembersIsNull() {
        double result = calculator.calculateMultiplier(null);

        assertEquals(1.0, result);
    }

    @Test
    void getActiveModifierNames_shouldReturnEmpty_whenNoMembers() {
        assertEquals(List.of(), calculator.getActiveModifierNames(List.of()));
    }

    @Test
    void getActiveModifierNames_shouldReturnEmpty_whenMembersIsNull() {
        assertEquals(List.of(), calculator.getActiveModifierNames(null));
    }

    @Test
    void getActiveModifierNames_shouldReturnBuffName_whenBuffActive() {
        List<ClanMember> members = members(10);
        stubDailyMissionCompletionForFirstMembers(5);
        when(quizStatRepository.findByUserIdInAndCompletedAtBetween(
                anyCollection(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        List<String> names = calculator.getActiveModifierNames(members);

        assertTrue(names.contains("Productivity Buff"));
    }

    @Test
    void getActiveModifierNames_shouldReturnDebuffName_whenDebuffActive() {
        List<ClanMember> members = List.of(member(1L));
        stubDailyMissionCompletionForFirstMembers(0);
        when(quizStatRepository.findByUserIdInAndCompletedAtBetween(
                anyCollection(), eq(CURRENT_WINDOW_START), any(LocalDateTime.class)))
                .thenReturn(List.of(stat(4, 10)));

        List<String> names = calculator.getActiveModifierNames(members);

        assertTrue(names.contains("Low Accuracy Penalty"));
    }

    @Test
    void calculateMultiplier_shouldApplyDailyMissionBuff_whenCompletionRateMeetsThreshold() {
        List<ClanMember> members = members(10);
        stubDailyMissionCompletionForFirstMembers(5);
        when(quizStatRepository.findByUserIdInAndCompletedAtBetween(
                anyCollection(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of());

        double result = calculator.calculateMultiplier(members);

        assertEquals(1.2, result);
    }

    @Test
    void calculateMultiplier_shouldNotApplyDailyMissionBuff_whenCompletionRateBelowThreshold() {
        List<ClanMember> members = members(10);
        stubDailyMissionCompletionForFirstMembers(4);
        when(quizStatRepository.findByUserIdInAndCompletedAtBetween(
                anyCollection(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of());

        double result = calculator.calculateMultiplier(members);

        assertEquals(1.0, result);
    }

    @Test
    void calculateMultiplier_shouldApplyAccuracyDebuff_whenCurrentAccuracyDecreases() {
        List<ClanMember> members = List.of(member(1L));
        stubDailyMissionCompletionForFirstMembers(0);
        stubAccuracyTrend(9, 10, 5, 10);

        double result = calculator.calculateMultiplier(members);

        assertEquals(0.8, result);
    }

    @Test
    void calculateMultiplier_shouldApplyAccuracyDebuff_whenCurrentAccuracyIsBelowThreshold() {
        List<ClanMember> members = List.of(member(1L));
        stubDailyMissionCompletionForFirstMembers(0);
        when(quizStatRepository.findByUserIdInAndCompletedAtBetween(
                anyCollection(),
                eq(CURRENT_WINDOW_START),
                any(LocalDateTime.class)
        )).thenReturn(List.of(stat(4, 10)));

        double result = calculator.calculateMultiplier(members);

        assertEquals(0.8, result);
    }

    @Test
    void calculateMultiplier_shouldStackBuffAndDebuff_whenBothConditionsAreActive() {
        List<ClanMember> members = members(10);
        stubDailyMissionCompletionForFirstMembers(5);
        stubAccuracyTrend(9, 10, 5, 10);

        double result = calculator.calculateMultiplier(members);

        assertEquals(0.96, result, 0.0001);
    }

    private void stubDailyMissionCompletionForFirstMembers(int completedCount) {
        when(completionRepository.countByUserIdInAndDateAssigned(anyCollection(), eq(TODAY)))
                .thenAnswer(invocation -> {
                    Collection<UUID> userIds = invocation.getArgument(0);
                    return userIds.stream()
                            .filter(userId -> userId.getLeastSignificantBits() <= completedCount)
                            .count();
                });
    }

    private void stubAccuracyTrend(
            int previousScore,
            int previousTotal,
            int currentScore,
            int currentTotal) {
        when(quizStatRepository.findByUserIdInAndCompletedAtBetween(
                anyCollection(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenAnswer(invocation -> {
            LocalDateTime start = invocation.getArgument(1);
            if (CURRENT_WINDOW_START.equals(start)) {
                return List.of(stat(currentScore, currentTotal));
            }
            return List.of(stat(previousScore, previousTotal));
        });
    }

    private ClanMemberQuizStat stat(int score, int total) {
        ClanMemberQuizStat s = new ClanMemberQuizStat();
        s.setScore(score);
        s.setTotal(total);
        return s;
    }

    private List<ClanMember> members(long count) {
        return LongStream.rangeClosed(1, count)
                .mapToObj(this::member)
                .toList();
    }

    private ClanMember member(long userId) {
        Clan clan = new Clan();
        clan.setId(1L);

        ClanMember member = new ClanMember();
        member.setClan(clan);
        member.setUserId(new UUID(0L, userId));
        member.setRole(ClanMember.Role.MEMBER);
        return member;
    }
}
