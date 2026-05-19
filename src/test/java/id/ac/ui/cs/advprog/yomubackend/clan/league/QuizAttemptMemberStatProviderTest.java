package id.ac.ui.cs.advprog.yomubackend.clan.league;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizAttemptMemberStatProviderTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Test
    void getStatForUser_shouldAggregateQuizAttempts() {
        QuizAttemptMemberStatProvider provider = new QuizAttemptMemberStatProvider(quizAttemptRepository);

        when(quizAttemptRepository.findByUserId(USER_ID))
                .thenReturn(List.of(attempt(8, 10), attempt(6, 10)));

        MemberStat result = provider.getStatForUser(USER_ID);

        assertEquals(USER_ID, result.userId());
        assertEquals(14, result.totalScore());
        assertEquals(2, result.quizCount());
        assertEquals(0.7, result.accuracy());
    }

    @Test
    void getStatForUser_shouldReturnZeroStat_whenUserHasNoQuizAttempts() {
        QuizAttemptMemberStatProvider provider = new QuizAttemptMemberStatProvider(quizAttemptRepository);

        when(quizAttemptRepository.findByUserId(USER_ID)).thenReturn(List.of());

        MemberStat result = provider.getStatForUser(USER_ID);

        assertEquals(USER_ID, result.userId());
        assertEquals(0, result.totalScore());
        assertEquals(0, result.quizCount());
        assertEquals(0.0, result.accuracy());
    }

    private QuizAttempt attempt(int score, int total) {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUserId(USER_ID);
        attempt.setScore(score);
        attempt.setTotal(total);
        return attempt;
    }

    // ── DummyMemberStatProvider ───────────────────────────────────────────────

    // UUID 00000000-0000-0000-0000-000000000001 → bucket=1
    // totalScore=(1%5+1)*100=200, quizCount=(1%3+1)*3=6, accuracy=0.5+(1%5*0.1)=0.6
    private static final UUID KNOWN_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final DummyMemberStatProvider dummyProvider = new DummyMemberStatProvider();

    @Test
    void dummy_getStatForUser_returnsNonNullStat() {
        assertNotNull(dummyProvider.getStatForUser(UUID.randomUUID()));
    }

    @Test
    void dummy_getStatForUser_preservesUserId() {
        UUID id = UUID.randomUUID();
        assertEquals(id, dummyProvider.getStatForUser(id).userId());
    }

    @Test
    void dummy_getStatForUser_deterministicForSameId() {
        MemberStat a = dummyProvider.getStatForUser(KNOWN_ID);
        MemberStat b = dummyProvider.getStatForUser(KNOWN_ID);
        assertEquals(a.totalScore(), b.totalScore());
        assertEquals(a.quizCount(), b.quizCount());
        assertEquals(a.accuracy(), b.accuracy(), 1e-9);
    }

    @Test
    void dummy_getStatForUser_knownId_returnsExpectedValues() {
        MemberStat stat = dummyProvider.getStatForUser(KNOWN_ID);
        assertEquals(200, stat.totalScore());
        assertEquals(6, stat.quizCount());
        assertEquals(0.6, stat.accuracy(), 1e-9);
    }

    @Test
    void dummy_getStatForUser_scoreIsPositiveMultipleOf100() {
        MemberStat stat = dummyProvider.getStatForUser(UUID.randomUUID());
        assertTrue(stat.totalScore() > 0);
        assertEquals(0, stat.totalScore() % 100);
    }

    @Test
    void dummy_getStatForUser_quizCountIsPositiveMultipleOf3() {
        MemberStat stat = dummyProvider.getStatForUser(UUID.randomUUID());
        assertTrue(stat.quizCount() > 0);
        assertEquals(0, stat.quizCount() % 3);
    }

    @Test
    void dummy_getStatForUser_accuracyBetween05And09() {
        MemberStat stat = dummyProvider.getStatForUser(UUID.randomUUID());
        assertTrue(stat.accuracy() >= 0.5 - 1e-9 && stat.accuracy() <= 0.9 + 1e-9);
    }
}
