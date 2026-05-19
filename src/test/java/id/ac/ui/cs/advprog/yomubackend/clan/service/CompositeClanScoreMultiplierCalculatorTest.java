package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserDailyMissionRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
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
    private UserDailyMissionRepository userDailyMissionRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    private CompositeClanScoreMultiplierCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new CompositeClanScoreMultiplierCalculator(
                userDailyMissionRepository,
                quizAttemptRepository,
                FIXED_CLOCK
        );
    }

    @Test
    void calculateMultiplier_shouldReturnNeutralMultiplier_whenClanHasNoMembers() {
        double result = calculator.calculateMultiplier(List.of());

        assertEquals(1.0, result);
    }

    @Test
    void calculateMultiplier_shouldApplyDailyMissionBuff_whenCompletionRateMeetsThreshold() {
        List<ClanMember> members = members(10);
        stubDailyMissionCompletionForFirstMembers(5);
        when(quizAttemptRepository.findByUserIdInAndCreatedAtBetween(
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
        when(quizAttemptRepository.findByUserIdInAndCreatedAtBetween(
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
        when(quizAttemptRepository.findByUserIdInAndCreatedAtBetween(
                anyCollection(),
                eq(CURRENT_WINDOW_START),
                any(LocalDateTime.class)
        )).thenReturn(List.of(attempt(4, 10)));

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
        when(userDailyMissionRepository.countByUserIdInAndDateAssignedAndIsCompletedTrue(anyCollection(), eq(TODAY)))
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
        when(quizAttemptRepository.findByUserIdInAndCreatedAtBetween(
                anyCollection(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenAnswer(invocation -> {
            LocalDateTime start = invocation.getArgument(1);
            if (CURRENT_WINDOW_START.equals(start)) {
                return List.of(attempt(currentScore, currentTotal));
            }
            return List.of(attempt(previousScore, previousTotal));
        });
    }

    private QuizAttempt attempt(int score, int total) {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setScore(score);
        attempt.setTotal(total);
        return attempt;
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
