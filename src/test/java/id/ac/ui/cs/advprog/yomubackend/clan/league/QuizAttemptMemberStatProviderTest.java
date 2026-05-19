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
}
