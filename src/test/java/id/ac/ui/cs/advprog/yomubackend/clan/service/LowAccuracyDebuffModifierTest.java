package id.ac.ui.cs.advprog.yomubackend.clan.service;

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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LowAccuracyDebuffModifierTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-05-07T12:00:00Z"), ZoneOffset.UTC);

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    private LowAccuracyDebuffModifier modifier;

    @BeforeEach
    void setUp() {
        modifier = new LowAccuracyDebuffModifier(quizAttemptRepository, FIXED_CLOCK);
    }

    @Test
    void calculateMultiplier_shouldReturnNeutral_whenMembersIsNull() {
        assertEquals(1.0, modifier.calculateMultiplier(null));
    }

    @Test
    void calculateMultiplier_shouldReturnNeutral_whenMembersIsEmpty() {
        assertEquals(1.0, modifier.calculateMultiplier(List.of()));
    }

    @Test
    void calculateMultiplier_shouldReturnNeutral_whenNoPreviousData_andAccuracyGood() {
        List<ClanMember> members = List.of(member(1L));

        // current window: good accuracy (9/10 = 0.9)
        // previous window: no attempts at all
        when(quizAttemptRepository.findByUserIdInAndCreatedAtBetween(
                anyCollection(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenAnswer(inv -> {
                    LocalDateTime start = inv.getArgument(1);
                    LocalDateTime currentStart = LocalDateTime.of(2026, 4, 30, 12, 0);
                    if (start.equals(currentStart)) {
                        return List.of(attempt(9, 10));
                    }
                    return List.of();
                });

        assertEquals(1.0, modifier.calculateMultiplier(members));
    }

    @Test
    void calculateMultiplier_shouldReturnNeutral_whenAccuracyImproved() {
        List<ClanMember> members = List.of(member(1L));

        // current: 8/10 = 0.8, previous: 6/10 = 0.6 — accuracy improved, no debuff
        when(quizAttemptRepository.findByUserIdInAndCreatedAtBetween(
                anyCollection(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenAnswer(inv -> {
                    LocalDateTime start = inv.getArgument(1);
                    LocalDateTime currentStart = LocalDateTime.of(2026, 4, 30, 12, 0);
                    if (start.equals(currentStart)) {
                        return List.of(attempt(8, 10));
                    }
                    return List.of(attempt(6, 10));
                });

        assertEquals(1.0, modifier.calculateMultiplier(members));
    }

    @Test
    void getModifierName_shouldReturnLowAccuracyPenalty() {
        assertEquals("Low Accuracy Penalty", modifier.getModifierName());
    }

    private QuizAttempt attempt(int score, int total) {
        QuizAttempt a = new QuizAttempt();
        a.setScore(score);
        a.setTotal(total);
        return a;
    }

    private ClanMember member(long id) {
        Clan clan = new Clan();
        clan.setId(1L);
        ClanMember m = new ClanMember();
        m.setClan(clan);
        m.setUserId(new UUID(0L, id));
        m.setRole(ClanMember.Role.MEMBER);
        return m;
    }
}
