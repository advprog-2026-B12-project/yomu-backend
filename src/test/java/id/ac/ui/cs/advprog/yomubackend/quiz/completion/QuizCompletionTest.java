package id.ac.ui.cs.advprog.yomubackend.quiz.completion;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizCompletionTest {

    @Test
    void isPerfectScore_returnsTrueWhenScoreEqualsTotal() {
        QuizCompletion completion = new QuizCompletion(
                UUID.randomUUID(),
                UUID.randomUUID(),
                3,
                3,
                LocalDateTime.now()
        );

        assertTrue(completion.isPerfectScore());
    }

    @Test
    void isPerfectScore_returnsFalseWhenTotalIsZero() {
        QuizCompletion completion = new QuizCompletion(
                UUID.randomUUID(),
                UUID.randomUUID(),
                0,
                0,
                LocalDateTime.now()
        );

        assertFalse(completion.isPerfectScore());
    }
}
